package com.compartetutiempo.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcuerdoRequest {
    
    @NotNull
    @Positive
    private Double horasAsignadas;
    @NotNull
    @NotBlank
    private String terminos;
}
