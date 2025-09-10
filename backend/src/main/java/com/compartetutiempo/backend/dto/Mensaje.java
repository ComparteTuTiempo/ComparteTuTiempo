package com.compartetutiempo.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Mensaje {
    private String contenido;
    private String remitente;
    private String destinatario;
    private LocalDateTime timestamp;
    private TipoMensaje tipo;

    public enum TipoMensaje {
        CHAT, JOIN, LEAVE
    }
}