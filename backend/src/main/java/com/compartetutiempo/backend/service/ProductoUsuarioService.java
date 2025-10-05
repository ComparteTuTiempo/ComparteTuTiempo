package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.dto.ProductoUsuarioDTO;
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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

@Service
public class ProductoUsuarioService {

    private final ProductoRepository productoRepository;
    private final ProductoUsuarioRepository productoUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConversacionRepository conversacionRepository;

    public ProductoUsuarioService(ProductoRepository productoRepository,
                                      ProductoUsuarioRepository productoUsuarioRepository,
                                      UsuarioRepository usuarioRepository,
                                      ConversacionRepository conversacionRepository) {
        this.productoRepository = productoRepository;
        this.productoUsuarioRepository = productoUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.conversacionRepository = conversacionRepository;
    }

    public ProductoUsuario adquirirProducto(Long productoId, Usuario comprador) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        
        Usuario propietario = producto.getPropietario();
        
        ProductoUsuario pendiente = productoUsuarioRepository.findByProductoPropietarioAndComprador(propietario, comprador)
        .orElse(null);

        if (producto.getEstado() != EstadoProducto.DISPONIBLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no está disponible");
        }else if (propietario.getId().equals(comprador.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes adquirir un producto que ya es tuyo");
        }else if(pendiente != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya has solicitado este producto");
        }else if(comprador.getNumeroHoras() < producto.getNumeroHoras()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes las suficientes horas para realizar esta transacción");
        }

        Conversacion conversacion = new Conversacion();
        conversacion.setParticipantes(List.of(producto.getPropietario(), comprador)); 
        conversacion.setTitulo(producto.getNombre() + " - " + comprador.getNombre() + " & " + producto.getPropietario().getNombre());;
        conversacion.setParticipantes(List.of(propietario,comprador));

        conversacionRepository.save(conversacion);

        // Crear la transacción
        ProductoUsuario transaccion = new ProductoUsuario();
        transaccion.setProducto(producto);
        transaccion.setConversacion(conversacion);
        transaccion.setComprador(comprador);
        transaccion.setEstado(EstadoProductoUsuario.PENDIENTE);
        producto.setEstado(EstadoProducto.RESERVADO);

        productoRepository.save(producto);
        return productoUsuarioRepository.save(transaccion);
    }

    public ProductoUsuario finalizarTransaccion(Integer transaccionId, Usuario propietario) {
        ProductoUsuario transaccion = productoUsuarioRepository.findById(transaccionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transacción no encontrada"));

        Usuario comprador = transaccion.getComprador();

        Integer precioHoras = transaccion.getProducto().getNumeroHoras();

        // Validar que el propietario sea el dueño del producto
        if (!transaccion.getProducto().getPropietario().getId().equals(propietario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para finalizar esta transacción");
        }

        transaccion.setEstado(EstadoProductoUsuario.FINALIZADA);
        comprador.setNumeroHoras(comprador.getNumeroHoras() - precioHoras);
        propietario.setNumeroHoras(propietario.getNumeroHoras() + precioHoras);

        usuarioRepository.saveAll(List.of(comprador,propietario));

        // Actualizamos también el estado del producto
        Producto producto = transaccion.getProducto();
        producto.setEstado(EstadoProducto.ENTREGADO);
        productoRepository.save(producto);

        return productoUsuarioRepository.save(transaccion);
    }

    @Transactional
    public void cancelarSolicitud(Integer transaccionId, String correoPropietario) {
        ProductoUsuario transaccion = productoUsuarioRepository.findById(transaccionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transacción no encontrada"));

        Producto producto = transaccion.getProducto();

        
        if (!producto.getPropietario().getCorreo().equals(correoPropietario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para cancelar esta transacción");
        }else if (transaccion.getEstado() == EstadoProductoUsuario.FINALIZADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede cancelar una transacción finalizada");
        }else if(transaccion.getEstado() == EstadoProductoUsuario.CANCELADA){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta transacción ya ha sido cancelada");
        }

        producto.setEstado(EstadoProducto.DISPONIBLE);
        productoUsuarioRepository.delete(transaccion);

        productoRepository.save(producto);
    }


    public List<ProductoUsuarioDTO> obtenerMisTransacciones(String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<ProductoUsuario> transacciones = productoUsuarioRepository
                .findByProductoPropietarioOrComprador(usuario, usuario);

        return transacciones.stream()
                .map(ProductoUsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
