package com.compartetutiempo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String fotoPerfil; 
}
