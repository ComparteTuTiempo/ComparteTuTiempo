package com.compartetutiempo.backend.dto;

import lombok.Data;

@Data
public class MensajeDTO {
    private String contenido;

    public enum TipoMensaje {
        CHAT, JOIN, LEAVE
    }
}