package com.compartetutiempo.backend.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipacionDTO {
    @Email
    private String correo;
    @NotBlank
    private String nombre;

    private String fotoPerfil;
    @NotNull
    private boolean asistio;
    
}
