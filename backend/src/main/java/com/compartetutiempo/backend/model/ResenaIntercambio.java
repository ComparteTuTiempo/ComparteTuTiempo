package com.compartetutiempo.backend.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "reseñas_intercambio")
public class ResenaIntercambio extends BaseEntity {
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "intercambio_id")
    private Intercambio intercambio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @Column(nullable = false)
    private int puntuacion; // 1-5

    @Column(nullable = false, length = 500)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}
