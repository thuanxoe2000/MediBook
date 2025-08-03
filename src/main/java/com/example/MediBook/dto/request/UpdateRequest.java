package com.example.MediBook.dto.request;

import com.example.MediBook.annotation.FieldMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldMatch(first = "password",second = "confirmPassword",message = "Mật khẩu không trùng khớp")
public class UpdateRequest {
    @Size(min = 8)
    private String password;
    private String confirmPassword;

    private String name;
    private String email;
    private String gender;
    private String phoneNumber;
    private String address;
    List<String> roles;

    private String token;
}
