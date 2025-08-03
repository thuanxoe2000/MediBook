package com.example.MediBook.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DpmUpdateRequest {
    private String name;
    private  String description;
    private int cost;
}
