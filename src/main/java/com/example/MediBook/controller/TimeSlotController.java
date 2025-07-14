package com.example.MediBook.controller;

import com.example.MediBook.dto.request.BookingRequest;
import com.example.MediBook.dto.response.BookingResponse;
import com.example.MediBook.dto.response.TimeSlotResponse;
import com.example.MediBook.service.TimeSlotService;
import com.example.MediBook.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/timeslots")
@RequiredArgsConstructor
public class TimeSlotController {
    private final TimeSlotService timeSlotService;

    @GetMapping("/{id}/get-slots")
    public ResponseEntity<List<TimeSlotResponse>> getAvailableSlots(
            @RequestParam LocalDate date,
            @PathVariable Long id){
        List<TimeSlotResponse> timeSlots= timeSlotService.getAvailableSlots(date,id);
        return ResponseEntity.ok(timeSlots);
    }

    @PostMapping("/book")
    public ResponseEntity<?> booking(@RequestBody BookingRequest request){
        timeSlotService.createPending(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user-booking")
    public ResponseEntity<List<BookingResponse>> userBooking() {
        List<BookingResponse> responses = timeSlotService.getUserBooking();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelBooking(@RequestParam Long bookingId){
        timeSlotService.cancel(bookingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/finish")
    public ResponseEntity<Void> finish(@RequestParam Long bookingId){
        timeSlotService.finish(bookingId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<?> confirmBooking(@PathVariable Long id,
                                            @RequestParam boolean isConfirm,
                                            @RequestBody(required = false) Map<String,String> note){
        String message = (note != null) ? note.get("note") : null;
        timeSlotService.confirmBooking(id, isConfirm, message);
        return ResponseEntity.noContent().build();
    }

}
