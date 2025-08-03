package com.example.MediBook.service;

import com.example.MediBook.dto.request.CreateDoctorRequest;
import com.example.MediBook.dto.request.SignupRequest;
import com.example.MediBook.dto.request.UpdateRequest;
import com.example.MediBook.dto.response.DoctorResponse;
import com.example.MediBook.dto.response.UserResponse;
import com.example.MediBook.entity.Doctor;
import com.example.MediBook.entity.Hospital;
import com.example.MediBook.entity.Role;
import com.example.MediBook.entity.User;
import com.example.MediBook.enums.TokenType;
import com.example.MediBook.mapper.Mapper;
import com.example.MediBook.repository.DoctorRepository;
import com.example.MediBook.repository.HospitalRepository;
import com.example.MediBook.repository.RoleRepository;
import com.example.MediBook.repository.UserRepository;
import com.example.MediBook.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    MailService mailService;
    TokenService tokenService;
    Mapper mapper;
    HospitalRepository hospitalRepository;
    DoctorRepository doctorRepository;
    UserUtil userUtil;
    RoleRepository roleRepository;

    @NonFinal
    @Value("${spring.mail.username}")
    String mailUsername;

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("ユーザーはすでに存在しています");
        }
        User user=User.builder()
                .name(request.getName())
                .createdAt(LocalDateTime.now())
                .gender(request.getGender())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .build();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false);

        Role role = roleRepository.findById("USER")
                .orElseThrow(() -> new RuntimeException("ロール USER が見つかりません"));
        user.setRoles(Set.of(role));

        userRepository.save(user);

        String token = tokenService.createToken(user, TokenType.VERIFY_EMAIL);
        sendVerificationLink(user.getEmail(), token);
    }

    void sendVerificationLink(String to, String token) {
        String link = "http://localhost:8080/auth/verify-email?token=" + token;
        String message = "以下のリンクをクリックしてアカウントを確認してください：\n" + link;
        SimpleMailMessage mail = sendEmail(mailUsername, to, "アカウント認証", message);
        mailService.sendMail(mail);
    }

    @Transactional
    public boolean verifyToken(String token) {
        Optional<User> userOpt = tokenService.validateToken(token, TokenType.VERIFY_EMAIL);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        user.setVerified(true);
        userRepository.save(user);
        tokenService.invalidate(token);
        return true;
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional
    public void deleteUnverifiedUsers() {
        LocalDateTime now = LocalDateTime.now();
        List<User> unverified = userRepository.findAll().stream()
                .filter(u -> !u.isVerified() && u.getCreatedAt() != null && u.getCreatedAt().isBefore(now.minusHours(1)))
                .toList();

        for (User user : unverified) {
            tokenService.deleteVerificationTokenByUser(user.getId());
            userRepository.delete(user);
        }
    }

    @Transactional
    public void sendResetPasswordToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.get().isVerified()) {
            return;
        }

        User user = userOpt.get();
        String token = tokenService.createToken(user, TokenType.RESET_PASSWORD);
        try {
            sendResetPasswordLink(user.getEmail(), token);
        } catch (Exception e) {
            throw new RuntimeException("メール送信に失敗しました: " + e.getMessage());
        }
    }

    private void sendResetPasswordLink(String to, String token) {
        String link = "http://localhost:8080/MediBook/reset-password.html?token=" + token;
        String content = "以下のリンクをクリックしてパスワードをリセットしてください：\n" + link;

        SimpleMailMessage mail = sendEmail(mailUsername, to, "パスワードのリセット", content);
        mailService.sendMail(mail);
    }

    public List<DoctorResponse> getAllDoctor() {
        List<Doctor> doctors = doctorRepository.findAll();

        return doctors.stream()
                .map(d -> DoctorResponse.builder()
                        .id(d.getId())
                        .name(d.getUser().getName())
                        .email(d.getUser().getEmail())
                        .gender(d.getUser().getGender())
                        .phoneNumber(d.getUser().getPhoneNumber())
                        .address(d.getUser().getAddress())
                        .enabled(d.getUser().isEnabled())
                        .hospitalName(d.getHospital().getName())
                        .build())
                .toList();
    }

    public void toggleUser(Long id, String message) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        if (user.isEnabled()) {
            user.setEnabled(false);
            sendBlockEmail(user.getEmail(), message);
        } else {
            user.setEnabled(true);
        }
        userRepository.save(user);
    }

    private void sendBlockEmail(String email, String message) {
        String subject = "アカウント停止の通知";
        SimpleMailMessage mail = sendEmail(mailUsername, email, subject, message);
        mailService.sendMail(mail);
    }

    public SimpleMailMessage sendEmail(String from, String to, String subject, String text) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(text);
        return mail;
    }

    public UserResponse getUser() {
        User user = userRepository.findById(userUtil.getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        return UserResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public void updateUser(UpdateRequest request) {
        User user = userRepository.findById(userUtil.getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());

        var roles=roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(String rawPassword, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<UserResponse> getUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(mapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public void createDoctor(CreateDoctorRequest request) {
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("病院が見つかりません"));
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setEnabled(true);
        user.setGender(request.getGender());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setVerified(true);
        Role doctorRole = roleRepository.findById("DOCTOR")
                .orElseThrow(() -> new RuntimeException("ロール DOCTOR が見つかりません"));
        user.setRoles(Set.of(doctorRole));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setHospital(hospital);
        doctorRepository.save(doctor);
    }
}
