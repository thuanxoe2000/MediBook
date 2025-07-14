package com.example.MediBook.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private Long dpmId;
    private Long timeSlotId;
    private String note;
}
