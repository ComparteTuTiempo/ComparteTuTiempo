package com.compartetutiempo.backend.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MensajeDTO {
    private Integer id;
    private String contenido;
    private Instant timestamp;
    private UsuarioDTO remitente;


    public enum TipoMensaje {
        CHAT, JOIN, LEAVE
    }
}