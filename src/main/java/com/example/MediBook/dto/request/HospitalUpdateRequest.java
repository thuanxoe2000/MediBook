package com.example.MediBook.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalUpdateRequest {
    private String imageUrl;
    @NotBlank
    private String name;
    @NotBlank
    private String specialty;
    @NotBlank
    private String address;
    @NotBlank
    private LocalTime startTime;
    @NotBlank
    private LocalTime endTime;
    @NotBlank
    private int cost;
    @NotBlank
    private String description;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String phoneNumber;
}
