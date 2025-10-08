package com.compartetutiempo.backend.model;

import java.time.LocalDateTime;

import com.compartetutiempo.backend.model.enums.EstadoEvento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "eventos")
@Getter
@Setter
public class Evento extends BaseEntity {

    @Column(nullable = false, length = 64)
    private String nombre;

    @Column(nullable = false, length = 512)
    private String descripcion;

    @Column(name = "duracion", nullable = false)
    @Positive
    private Integer duracion;

    @Column(nullable = false, length = 255)
    private String ubicacion;

    @PositiveOrZero
    private Integer capacidad;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    @Enumerated(value = EnumType.STRING)
    private EstadoEvento estadoEvento = EstadoEvento.DISPONIBLE;

    @ManyToOne
    @JoinColumn(name="usuario_id", referencedColumnName = "id", nullable=false)
    private Usuario organizador;

}
