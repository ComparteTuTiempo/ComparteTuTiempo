package com.compartetutiempo.backend.dto;

import com.compartetutiempo.backend.model.IntercambioUsuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;

import lombok.Data;

@Data
public class IntercambioUsuarioDTO {
    private Long usuarioId;
    private String nombreUsuario;
    private EstadoIntercambio estado;
    private Double horasAsignadas;

    public static IntercambioUsuarioDTO fromEntity(IntercambioUsuario iu) {
        IntercambioUsuarioDTO dto = new IntercambioUsuarioDTO();
        dto.setUsuarioId(iu.getUsuario().getId());
        dto.setNombreUsuario(iu.getUsuario().getNombre());
        dto.setEstado(iu.getEstado());
        dto.setHorasAsignadas(iu.getHorasAsignadas());
        return dto;
    }
}
