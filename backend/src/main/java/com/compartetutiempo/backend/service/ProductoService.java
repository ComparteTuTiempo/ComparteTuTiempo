package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.model.Producto;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoProducto;
import com.compartetutiempo.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto crear(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id).orElseThrow();
    }

    public List<Producto> obtenerPorUsuario(Usuario user) {
        return productoRepository.findByUser(user);
    }

    public Producto actualizarProducto(Long id, Producto productoModificado, Usuario user) {
        Producto producto = obtenerPorId(id);

        if (!producto.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permisos para modificar este producto");
        }

        producto.setNombre(productoModificado.getNombre());
        producto.setDescripcion(productoModificado.getDescripcion());
        producto.setNumeroHoras(productoModificado.getNumeroHoras());
        producto.setEstado(productoModificado.getEstado());

        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id, Usuario user) {
        Producto producto = obtenerPorId(id);

        if (!producto.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permisos para eliminar este producto");
        }

        productoRepository.delete(producto);
    }

    public List<Producto> obtenerHistorial(Usuario user) {
        return productoRepository.findByUserAndEstado(user, EstadoProducto.ENTREGADO);
    }

    public void eliminarProductoComoAdmin(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        productoRepository.delete(producto);
    }

}
