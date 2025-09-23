package com.compartetutiempo.backend.model;

import com.compartetutiempo.backend.model.enums.EstadoVerificacion;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Verificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String documentoURL; // ruta de la foto subida

    @Enumerated(EnumType.STRING)
    private EstadoVerificacion estado = EstadoVerificacion.PENDIENTE;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private Date fechaSolicitud = new Date();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDocumentoURL() {
        return documentoURL;
    }

    public void setDocumentoURL(String documentoURL) {
        this.documentoURL = documentoURL;
    }

    public EstadoVerificacion getEstado() { return estado; }
    public void setEstado(EstadoVerificacion estado) { this.estado = estado; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Date getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Date fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
}
