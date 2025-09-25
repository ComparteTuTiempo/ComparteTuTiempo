package com.compartetutiempo.backend.dto;

import java.util.Date;

import com.compartetutiempo.backend.model.Producto;
import com.compartetutiempo.backend.model.enums.EstadoProducto;

import lombok.Data;

@Data
public class ProductoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Double numeroHoras;
    private EstadoProducto estado;
    private Date fechaPublicacion;

    // Datos del usuario creador
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioCorreo;

    public static ProductoDTO fromEntity(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setNumeroHoras(producto.getNumeroHoras());
        dto.setEstado(producto.getEstado());
        dto.setFechaPublicacion(producto.getFechaPublicacion());

        if (producto.getUser() != null) {
            dto.setUsuarioId(producto.getUser().getId());
            dto.setUsuarioNombre(producto.getUser().getNombre());
            dto.setUsuarioCorreo(producto.getUser().getCorreo());
        }

        return dto;
    }
}

