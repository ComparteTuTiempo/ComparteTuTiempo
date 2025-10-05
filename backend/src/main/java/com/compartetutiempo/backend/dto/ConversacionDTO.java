package com.compartetutiempo.backend.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.compartetutiempo.backend.model.Conversacion;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConversacionDTO {
    private Integer id;
    private String titulo;

    private List<UsuarioDTO> participantes;

    @Data
    @AllArgsConstructor
    public static class UsuarioDTO {
        private String correo;
        private String nombre;
        private String fotoPerfil;
    }

    public static ConversacionDTO fromEntity(Conversacion c) {
        List<UsuarioDTO> participantesDTO = c.getParticipantes()
            .stream()
            .map(u -> new UsuarioDTO(u.getCorreo(), u.getNombre(), u.getFotoPerfil()))
//            .collect(Collectors.toList());
        .toList();
        return new ConversacionDTO(c.getId(),c.getTitulo() ,participantesDTO);
    }
}

