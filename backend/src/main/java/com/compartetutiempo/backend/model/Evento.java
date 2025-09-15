package com.compartetutiempo.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.compartetutiempo.backend.model.enums.EstadoEvento;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "eventos")
@Getter
@Setter
public class Evento extends BaseEntity{
    
    @Column(nullable = false, length = 64)
    private String nombre;

    @Column(nullable = false, length = 512)
    private String descripcion;

    @Column(name = "duracion", nullable = false,scale = 2)
    private Double duracion;

    @DateTimeFormat
    @Column(name = "fecha_evento",nullable = false)
    @FutureOrPresent
    private LocalDateTime fechaEvento;

    @Enumerated(value = EnumType.STRING)
    private EstadoEvento estadoEvento;

    @ManyToOne
    @JoinColumn(name="usuario_id",referencedColumnName = "id",nullable=false)
    private Usuario organizador;

    @ManyToMany
    @JoinTable(
        name = "eventos_participantes",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    @JsonIgnore
    private List<Usuario> participantes;
}
