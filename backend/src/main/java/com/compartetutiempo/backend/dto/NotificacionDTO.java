package com.compartetutiempo.backend.dto;

import java.time.Instant;

import com.compartetutiempo.backend.model.Notificacion;
import com.compartetutiempo.backend.model.enums.TipoNotificacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionDTO {
    private Integer id;
    private TipoNotificacion tipo;
    private String contenido;
    private Instant timestamp;
    private boolean leida;
    private Integer referenciaId;

    public static NotificacionDTO fromEntity(Notificacion notificacion) {
        NotificacionDTO dto = new NotificacionDTO();

        dto.setId(notificacion.getId());
        dto.setContenido(notificacion.getContenido());
        dto.setTimestamp(notificacion.getTimestamp());
        dto.setReferenciaId(notificacion.getReferenciaId());
        dto.setTipo(notificacion.getTipo());
        
        return dto;
        
    }
}