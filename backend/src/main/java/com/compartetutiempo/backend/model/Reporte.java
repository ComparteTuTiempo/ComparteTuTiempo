package com.compartetutiempo.backend.model;

import java.util.Date;

import com.compartetutiempo.backend.model.enums.EstadoReporte;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reportes")
@Getter
@Setter
public class Reporte extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reportado_id", referencedColumnName = "id")
    private Usuario usuarioReportado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reportador_id", referencedColumnName = "id")
    private Usuario usuarioReportador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReporte estado = EstadoReporte.PENDIENTE;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();
}
