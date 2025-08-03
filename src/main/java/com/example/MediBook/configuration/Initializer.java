package com.example.MediBook.configuration;

import com.example.MediBook.entity.Role;
import com.example.MediBook.entity.User;
import com.example.MediBook.repository.RoleRepository;
import com.example.MediBook.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Initializer implements ApplicationRunner {

    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    final RoleRepository roleRepository;

    static String[] ROLES = {
            "USER", "DOCTOR", "DIRECTOR", "DEPARTMENT_HEAD", "ADMIN"
    };

    @Value("${admin.email}")
    String adminEmail;

    @Value("${admin.password}")
    String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        createRoles();
        createAdminIfNotExist();
    }

    private void createRoles() {
        for (String role : ROLES) {
            if (!roleRepository.existsById(role)) {
                Role newRole = Role.builder()
                        .name(role)
                        .build();
                roleRepository.save(newRole);
            }
        }
    }

    private void createAdminIfNotExist() {
        if (adminEmail == null || adminPassword == null) {
            throw new IllegalStateException("admin.email または admin.password の設定が不足しています");
        }

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            Role adminRole = roleRepository.findById("ADMIN").orElseThrow();
            User admin = new User();
            admin.setName("admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRoles(Set.of(adminRole));
            admin.setVerified(true);
            admin.setEnabled(true);
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("\u001B[32mAdmin created!\u001B[0m");
        }
    }
}
