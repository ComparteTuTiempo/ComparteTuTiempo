package com.compartetutiempo.backend.dto;

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
}
