package com.compartetutiempo.backend.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mensajes")
@Getter
@Setter
public class Mensaje extends BaseEntity{

    @ManyToOne(optional = false)
    @JoinColumn(name = "conversacion_id", nullable = false)
    @JsonIgnore
    private Conversacion conversacion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;

    @Column(nullable = false, length = 500)
    private String contenido;

    @Column(nullable = false)
    private Instant timestamp = Instant.now();
}
