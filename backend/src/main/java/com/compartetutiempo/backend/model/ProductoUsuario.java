package com.compartetutiempo.backend.model;

import com.compartetutiempo.backend.model.enums.EstadoProductoUsuario;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "productos_usuarios")
public class ProductoUsuario extends BaseEntity{


    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Comprador
    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private Usuario comprador;

    @Enumerated(EnumType.STRING)
    private EstadoProductoUsuario estado;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "conversacion_id")
    private Conversacion conversacion;
}
