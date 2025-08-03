package com.example.MediBook.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Department> department;

    @OneToOne
    @JoinColumn(name = "director_id")
    private Doctor director;
}
