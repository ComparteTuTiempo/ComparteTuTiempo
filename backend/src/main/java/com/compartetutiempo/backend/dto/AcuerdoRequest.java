package com.compartetutiempo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcuerdoRequest {
    private Double horasAsignadas;
    private String terminos;
}
