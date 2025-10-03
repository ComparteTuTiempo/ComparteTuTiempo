package com.compartetutiempo.backend.model;

import java.time.Instant;

import com.compartetutiempo.backend.model.enums.TipoNotificacion;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "notificaciones")
public class Notificacion extends BaseEntity{

    @Enumerated(value = EnumType.STRING)
    private TipoNotificacion tipo;    

    @NotBlank  
    private String contenido;

    private Instant timestamp;

    @ManyToOne
    private Usuario usuarioDestino;

    private Integer referenciaId; 
}
