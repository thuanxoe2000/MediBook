package com.example.MediBook.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {
    private String name;
    private String description;
    private Set<String> permission;
}
