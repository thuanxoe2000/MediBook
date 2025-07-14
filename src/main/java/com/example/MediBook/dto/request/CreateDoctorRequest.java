package com.example.MediBook.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDoctorRequest {
    @NotNull(message = "空欄にできません")
    @Size(min = 8)
    private String password;

    @NotBlank(message = "空欄にできません")
    private String name;

    @Email
    @NotBlank(message = "空欄にできません")
    private String email;

    @NotBlank
    private String gender;

    @NotNull(message = "空欄にできません")
    private String phoneNumber;

    @NotBlank(message = "空欄にできません")
    private String address;

    private Long hospitalId;
}
