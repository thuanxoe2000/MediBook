package com.example.MediBook.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Role {
    @Id
    private String name;
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Permission> permissions;
}
