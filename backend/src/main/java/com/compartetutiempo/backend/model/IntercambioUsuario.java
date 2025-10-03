package com.compartetutiempo.backend.model;

import com.compartetutiempo.backend.model.enums.EstadoIntercambio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "intercambio_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntercambioUsuario extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "intercambio_id", referencedColumnName = "id")
    private Intercambio intercambio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoIntercambio estado; 

    @Column(length = 512)
    private String terminos;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "conversacion_id",referencedColumnName = "id",unique=true)
    private Conversacion conversacion;

    @Column(name = "horas_asignadas")
    private Double horasAsignadas = 0.0;
}
