package com.example.MediBook.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentRequest {
    @NotBlank(message = "空欄にできません")
    private String name;

    @NotBlank(message = "空欄にできません")
    private String description;

    @NotNull(message = "空欄にできません")
    private Integer cost;

    @NotNull(message = "空欄にできません")
    private Long hospitalId;

    @Valid
    @NotNull(message = "空欄にできません")
    private CreateDoctorRequest head;
}
