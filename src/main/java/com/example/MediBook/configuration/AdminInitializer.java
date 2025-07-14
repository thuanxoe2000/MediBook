package com.example.MediBook.configuration;

import com.example.MediBook.entity.User;
import com.example.MediBook.enums.Role;
import com.example.MediBook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (adminEmail == null || adminPassword == null) {
            throw new IllegalStateException("Thiếu cấu hình admin.email hoặc admin.password");
        }

        if (userRepository.findByEmail(adminEmail).isEmpty()){
            User admin=new User();
            admin.setName("admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setVerified(true);
            userRepository.save(admin);
            System.out.println("\u001B[32mAdmin created!\u001B[0m");
        }
    }
}
