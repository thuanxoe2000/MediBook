package com.example.MediBook.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalRequest {
    private String imageUrl;

    @NotBlank(message = "空欄にできません")
    private String name;

    @NotBlank(message = "空欄にできません")
    private String address;

    @NotNull(message = "空欄にできません")
    private LocalTime startTime;

    @NotNull(message = "空欄にできません")
    private LocalTime endTime;

    @NotNull(message = "空欄にできません")
    private Integer cost;

    @NotBlank(message = "空欄にできません")
    private String description;

    @Email
    @NotBlank(message = "空欄にできません")
    private String email;

    @NotNull(message = "空欄にできません")
    private String phoneNumber;

    @Valid
    @NotNull(message = "空欄にできません")
    private CreateDoctorRequest director;
}
