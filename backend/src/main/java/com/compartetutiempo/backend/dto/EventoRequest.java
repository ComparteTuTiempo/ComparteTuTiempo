package com.compartetutiempo.backend.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

@Data
public class EventoRequest{
    @Column(nullable = false, length = 64)
    private String nombre;

    @Column(nullable = false, unique = true)
    @Email
    private String correoOrganizador;

    @Column(nullable = false, length = 512)
    private String descripcion;

    @Column(name = "duracion", nullable = false,scale = 2)
    private Double duracion;

    @DateTimeFormat
    @Column(name = "fecha_evento",nullable = false)
    @FutureOrPresent
    private LocalDateTime fechaEvento;
    
}
