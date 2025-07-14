package com.example.MediBook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {
    private Long id;
    private String name;
    private String description;
    private int cost;
    private UserResponse head;
    private List<TimeSlotResponse> timeSlots;
    private List<BookingResponse> bookingResponses;
}
