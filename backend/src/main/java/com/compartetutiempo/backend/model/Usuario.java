package com.compartetutiempo.backend.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    @Column
    private LocalDate fechaNacimiento;

    @Column(length = 500)
    private String biografia;

    @Column
    private String fotoPerfil;

    @Column
    private String ubicacion;

    @Column(nullable = false)
    private String metodoAutenticacion;
    
}
