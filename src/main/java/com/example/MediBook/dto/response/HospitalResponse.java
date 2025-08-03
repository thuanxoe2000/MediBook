package com.example.MediBook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalResponse {
    private Long id;
    private String imageUrl;
    private String name;
    private String address;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer cost;
    private String description;
    private String email;
    private String phoneNumber;
    private List<DepartmentResponse> responses;
}
