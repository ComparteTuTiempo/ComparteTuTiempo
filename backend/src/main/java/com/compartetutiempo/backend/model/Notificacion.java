package com.compartetutiempo.backend.model;

import java.time.Instant;

import org.hibernate.validator.constraints.Length;

import com.compartetutiempo.backend.model.enums.TipoNotificacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "notificaciones")
public class Notificacion extends BaseEntity{

    @Enumerated(value = EnumType.STRING)
    private TipoNotificacion tipo;    
    @Length( max = 64)
    @NotBlank  
    private String contenido;
    private Instant timestamp;
    @NotNull
    private boolean leida;

    @ManyToOne
    private Usuario usuarioDestino;

    @NotNull
    private Integer referenciaId; 
}
