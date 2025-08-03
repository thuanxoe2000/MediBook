package com.example.MediBook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private boolean enabled;
    private String gender;
    private String phoneNumber;
    private String address;
    private LocalDateTime createdAt;
    private Set<RoleResponse> roles;

}
