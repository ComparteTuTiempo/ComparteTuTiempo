package com.compartetutiempo.backend.model;

import java.util.ArrayList;
import java.util.List;

import com.compartetutiempo.backend.model.enums.TipoConversacion;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "conversaciones")
@Getter
@Setter
public class Conversacion extends BaseEntity{

    @Column(length = 128)
    private String titulo;

    @ManyToMany
    @JoinTable(
        name = "conversationes_usuarios",
        joinColumns = @JoinColumn(name = "conversacion_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    @JsonIgnore
    private List<Usuario> participantes = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    private TipoConversacion tipo = TipoConversacion.PRIVADA;
    
    
}
