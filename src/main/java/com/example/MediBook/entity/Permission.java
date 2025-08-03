package com.example.MediBook.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Permission {
    @Id
    private String name;
    private String description;
}
