package com.example.MediBook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String userName;
    private String email;
    private LocalDate date;
    private LocalTime time;
    private String status;
    private String statusMessage;
    private String note;
    private LocalDateTime createdAt;
    private String code;
}
