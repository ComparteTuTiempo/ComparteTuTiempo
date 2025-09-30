package com.compartetutiempo.backend.dto;

import com.compartetutiempo.backend.model.ProductoUsuario;
import com.compartetutiempo.backend.model.enums.EstadoProductoUsuario;
import lombok.Data;

@Data
public class ProductoUsuarioDTO {
    private Integer id;
    private Integer productoId;
    private String productoNombre;
    private String productoDescripcion;
    private Double productoHoras;
    private String propietarioNombre;
    private String propietarioCorreo;
    private String compradorNombre;
    private String compradorCorreo;
    private EstadoProductoUsuario estado;

    public static ProductoUsuarioDTO fromEntity(ProductoUsuario transaccion) {
        ProductoUsuarioDTO dto = new ProductoUsuarioDTO();
        dto.setId(transaccion.getId());
        dto.setProductoId(transaccion.getProducto().getId());
        dto.setProductoNombre(transaccion.getProducto().getNombre());
        dto.setProductoDescripcion(transaccion.getProducto().getDescripcion());
        dto.setProductoHoras(transaccion.getProducto().getNumeroHoras());
        dto.setPropietarioNombre(transaccion.getProducto().getPropietario().getNombre());
        dto.setPropietarioCorreo(transaccion.getProducto().getPropietario().getCorreo());
        dto.setCompradorNombre(transaccion.getComprador().getNombre());
        dto.setCompradorCorreo(transaccion.getComprador().getCorreo());
        dto.setEstado(transaccion.getEstado());
        return dto;
    }
}
