package com.example.MediBook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {
    private Long id;
    private String name;
    private String email;
    private String gender;
    private String phoneNumber;
    private String address;
    private boolean enabled;
    private String hospitalName;
    private String departmentName;
    private List<RoleResponse> roles;
}
