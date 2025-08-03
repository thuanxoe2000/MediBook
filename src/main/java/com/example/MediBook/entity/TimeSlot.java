package com.example.MediBook.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private LocalTime time;

    private boolean available;

    @OneToMany(mappedBy = "timeSlot")
    private List<Booking> bookings;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Department department;


}
