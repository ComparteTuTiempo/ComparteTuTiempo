package com.compartetutiempo.backend.model;

import java.util.Date;

import com.compartetutiempo.backend.model.enums.EstadoProducto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "producto")
@Getter
@Setter
public class Producto extends BaseEntity {

    @Column(length = 64, nullable = false)
    private String nombre;

    @Column(length = 256, nullable = false)
    private String descripcion;

    @Column(name = "fecha_publicacion")
    private Date fechaPublicacion;

    @Column(name = "numero_horas", precision = 2)
    @Positive
    private Double numeroHoras;

    @Enumerated(value = EnumType.STRING)
    private EstadoProducto estado;

    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Usuario propietario;

}
