package com.compartetutiempo.backend.dto;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

import com.compartetutiempo.backend.model.Evento;
import com.compartetutiempo.backend.model.enums.EstadoEvento;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EventoResponse {

    private Integer id;

    @NotBlank
    private String nombre;

    @NotBlank
    @Length(max = 512)
    private String descripcion;
    @NotNull
    private Integer duracion;

    @Positive
    private Integer capacidad;

    @FutureOrPresent
    private LocalDateTime fechaEvento;

    @NotBlank
    private String ubicacion;

    @NotNull
    private EstadoEvento estadoEvento;

    @NotNull
    private UsuarioDTO organizador;

    public static EventoResponse mapToDTO(Evento evento) {
    return new EventoResponse(
            evento.getId(),
            evento.getNombre(),
            evento.getDescripcion(),
            evento.getUbicacion(),
            evento.getDuracion(),
            evento.getFechaEvento(),
            evento.getEstadoEvento(),
            new UsuarioDTO(
                    null,
                    evento.getOrganizador().getNombre(),
                    evento.getOrganizador().getCorreo(),
                    null,
                    evento.getOrganizador().getFotoPerfil(),
                    evento.getOrganizador().isVerificado(),
                    evento.getOrganizador().isActivo(),
                    evento.getOrganizador().getBiografia(),
                    evento.getOrganizador().getFechaNacimiento()
            ),
            evento.getCapacidad()
    );
}

    public EventoResponse(Integer id, String nombre, String descripcion, String ubicacion, Integer duracion,
            LocalDateTime fechaEvento,
            EstadoEvento estadoEvento, UsuarioDTO usuarioDTO,Integer capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.duracion = duracion;
        this.fechaEvento = fechaEvento;
        this.estadoEvento = estadoEvento;
        this.organizador = usuarioDTO;
        this.capacidad=capacidad;
    }
}
