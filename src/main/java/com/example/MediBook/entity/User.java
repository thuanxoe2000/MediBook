package com.example.MediBook.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String gender;
    @Column(name = "email",unique = true,columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    private String email;
    private String phoneNumber;
    private String address;
    private String password;
    private boolean enabled=true;
    private boolean verified;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Role> roles;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt=LocalDateTime.now();
    }

}
