package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.dto.ProductoDTO;
import com.compartetutiempo.backend.model.Conversacion;
import com.compartetutiempo.backend.model.Producto;
import com.compartetutiempo.backend.model.ProductoUsuario;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoProducto;
import com.compartetutiempo.backend.model.enums.EstadoProductoUsuario;
import com.compartetutiempo.backend.repository.ConversacionRepository;
import com.compartetutiempo.backend.repository.ProductoRepository;
import com.compartetutiempo.backend.repository.ProductoUsuarioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConversacionRepository conversacionRepository;
    private final ProductoUsuarioRepository productoUsuarioRepository;

    public ProductoService(ProductoRepository productoRepository
        ,UsuarioRepository usuarioRepository,
        ConversacionRepository conversacionRepository,
        ProductoUsuarioRepository productoUsuarioRepository) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.conversacionRepository = conversacionRepository;
        this.productoUsuarioRepository = productoUsuarioRepository;
    }

    public Producto crear(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public ProductoDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow();
        return ProductoDTO.fromEntity(producto,null);
    }


    @Transactional
    public ProductoDTO solicitarProducto(Long productoId, String correoComprador) {
        Usuario comprador = usuarioRepository.findByCorreo(correoComprador)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getPropietario().getId().equals(comprador.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes reservar tu propio producto");
        }

        if (producto.getEstado() != EstadoProducto.DISPONIBLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no está disponible");
        }

        // Marcar producto como reservado
        producto.setEstado(EstadoProducto.RESERVADO);

        // Crear transacción
        ProductoUsuario transaccion = new ProductoUsuario();
        transaccion.setProducto(producto);
        transaccion.setComprador(comprador);
        transaccion.setEstado(EstadoProductoUsuario.PENDIENTE);

        // Crear conversación asociada
        Conversacion conversacion = new Conversacion();
        conversacion.setParticipantes(List.of(comprador, producto.getPropietario()));
        conversacionRepository.save(conversacion);

        transaccion.setConversacion(conversacion);

        productoUsuarioRepository.save(transaccion);
        productoRepository.save(producto);

        return ProductoDTO.fromEntity(producto, transaccion);
    }

    public List<Producto> obtenerPorUsuario(Usuario user) {
        return productoRepository.findByPropietario(user);
    }

    public ProductoDTO actualizarProducto(Long id, Producto productoModificado, Usuario user) {
        Producto producto = productoRepository.findById(id).orElseThrow();

        if (!producto.getPropietario().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permisos para modificar este producto");
        }

        producto.setNombre(productoModificado.getNombre());
        producto.setDescripcion(productoModificado.getDescripcion());
        producto.setNumeroHoras(productoModificado.getNumeroHoras());
        producto.setEstado(productoModificado.getEstado());

        productoRepository.save(producto);
        return ProductoDTO.fromEntity(producto,null);
    }

    public void eliminarProducto(Long id, Usuario user) {
        Producto producto = productoRepository.findById(id).orElseThrow();

        if (!producto.getPropietario().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permisos para eliminar este producto");
        }

        productoUsuarioRepository.deleteByProducto(producto);
        productoRepository.delete(producto);
    }

    public List<Producto> obtenerHistorial(Usuario user) {
        return productoRepository.findByPropietarioAndEstado(user, EstadoProducto.ENTREGADO);
    }

    public void eliminarProductoComoAdmin(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        productoUsuarioRepository.deleteByProducto(producto);
        productoRepository.delete(producto);
    }

}
