package com.compartetutiempo.backend.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "conversaciones")
@Getter
@Setter
public class Conversacion extends BaseEntity{

    private String titulo;

    @ManyToMany
    @JoinTable(
        name = "conversationes_usuarios",
        joinColumns = @JoinColumn(name = "conversacion_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    @JsonIgnore
    private List<Usuario> participantes = new ArrayList<>();
}
