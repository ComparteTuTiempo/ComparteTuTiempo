package com.compartetutiempo.backend.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDetalleDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String fotoPerfil;
    private String ubicacion;
    private boolean verificado;
    private boolean activo;
    private String biografia;
    private LocalDate fechaNacimiento;
    private Integer numeroHoras;
    private List<String> roles;
}
