package com.compartetutiempo.backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String ubicacion;
    private String fotoPerfil;
    private boolean verificado;
    private boolean activo;
    private String biografia;
    private LocalDate fechaNacimiento;
}
