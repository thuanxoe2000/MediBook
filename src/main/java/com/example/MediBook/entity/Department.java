package com.example.MediBook.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    @JsonIgnore
    private Hospital hospital;

    @OneToOne
    @JoinColumn(name = "head_id")
    private User head;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<TimeSlot> timeSlots;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;
}
