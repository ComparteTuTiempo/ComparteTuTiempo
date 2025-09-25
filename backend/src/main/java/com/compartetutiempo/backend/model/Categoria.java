package com.compartetutiempo.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Categoria extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

}
