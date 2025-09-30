package com.compartetutiempo.backend.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.model.enums.ModalidadServicio;
import com.compartetutiempo.backend.model.enums.TipoIntercambio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "intercambio")
@Getter
@Setter
public class Intercambio extends BaseEntity{

    @Column(length = 64, nullable=false)
    private String nombre;

    @Column(length = 256,nullable=false)
    private String descripcion;

    @Column(name = "fecha_publicacion")
    private Date fechaPublicacion;

    @Column(name = "numero_horas",precision = 2)
    private Double numeroHoras;

    @Enumerated(value= EnumType.STRING)
    private EstadoIntercambio estado;

    @Enumerated(value= EnumType.STRING)
    private TipoIntercambio tipo;

    @Enumerated(value= EnumType.STRING)
    private ModalidadServicio modalidad;

    @JoinColumn(name="usuario_id",referencedColumnName = "id",nullable=false)
    @ManyToOne(optional=false)
    private Usuario user;

    @ManyToMany
    @JoinTable(
    name = "intercambio_categorias",
    joinColumns = @JoinColumn(name = "intercambio_id"),
    inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias = new HashSet<>();
    
}
