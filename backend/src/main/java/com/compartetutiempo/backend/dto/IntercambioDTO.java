package com.compartetutiempo.backend.dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.IntercambioUsuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.model.enums.ModalidadServicio;
import com.compartetutiempo.backend.model.enums.TipoIntercambio;

import lombok.Data;

@Data
public class IntercambioDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String correoOfertante;
    private Date fechaPublicacion;
    private Integer numeroHoras;
    private String nombreOfertante;
    private EstadoIntercambio estado;
    private String fotoPerfil;
    private TipoIntercambio tipo;
    private ModalidadServicio modalidad;
    private List<IntercambioUsuarioDTO> participantes; 
    private List<Long> categorias;

    public static IntercambioDTO fromEntity(Intercambio intercambio, List<IntercambioUsuario> participantes) {
        IntercambioDTO dto = new IntercambioDTO();
        dto.setId(intercambio.getId());
        dto.setNombre(intercambio.getNombre());
        dto.setDescripcion(intercambio.getDescripcion());
        dto.setFechaPublicacion(intercambio.getFechaPublicacion());
        dto.setNumeroHoras(intercambio.getNumeroHoras());
        dto.setEstado(intercambio.getEstado());
        dto.setTipo(intercambio.getTipo());
        dto.setModalidad(intercambio.getModalidad());
        dto.setCorreoOfertante(intercambio.getUser().getCorreo());
        dto.setNombreOfertante(intercambio.getUser().getNombre());
        dto.setFotoPerfil(intercambio.getUser().getFotoPerfil());

        dto.setParticipantes(
            participantes.stream()
                .map(IntercambioUsuarioDTO::fromEntity)
                .collect(Collectors.toList())
        );

        return dto;
    }
}
