package com.compartetutiempo.backend.dto;

import com.compartetutiempo.backend.model.IntercambioUsuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.model.enums.ModalidadServicio;

import lombok.Data;

@Data
public class IntercambioUsuarioDTO {
    private Integer id;
    private Integer intercambioId;
    private String intercambioNombre;
    private String intercambioDescripcion;
    private Double intercambioHoras;
    private EstadoIntercambio intercambioEstado;
    private ModalidadServicio intercambioModalidad;
    private Long creadorId;
    private String creadorNombre;
    private String creadorCorreo;
    private EstadoIntercambio estado;
    private Double horasAsignadas;

    public static IntercambioUsuarioDTO fromEntity(IntercambioUsuario iu) {
        IntercambioUsuarioDTO dto = new IntercambioUsuarioDTO();
        dto.setId(iu.getId());
        dto.setIntercambioId(iu.getIntercambio().getId());
        dto.setIntercambioNombre(iu.getIntercambio().getNombre());
        dto.setIntercambioDescripcion(iu.getIntercambio().getDescripcion());
        dto.setIntercambioHoras(iu.getIntercambio().getNumeroHoras());
        dto.setIntercambioEstado(iu.getIntercambio().getEstado());
        dto.setIntercambioModalidad(iu.getIntercambio().getModalidad());
        dto.setCreadorId(iu.getIntercambio().getUser().getId());
        dto.setCreadorNombre(iu.getIntercambio().getUser().getNombre());
        dto.setCreadorCorreo(iu.getIntercambio().getUser().getCorreo());
        dto.setEstado(iu.getEstado());
        dto.setHorasAsignadas(iu.getHorasAsignadas());
        
        
        return dto;
    }
}
