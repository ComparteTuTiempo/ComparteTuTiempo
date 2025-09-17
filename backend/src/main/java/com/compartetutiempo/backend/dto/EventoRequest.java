package com.compartetutiempo.backend.dto;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EventoRequest{

    @NotBlank
    private String nombre;
   
    @Email
    private String correoOrganizador;

    @NotBlank
    @Length(max = 512)
    private String descripcion;

    @NotBlank
    private String ubicacion;

    @NotNull
    private Double duracion;

    @DateTimeFormat
    @FutureOrPresent
    private LocalDateTime fechaEvento;
    
}
