package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.dto.ProductoUsuarioDTO;
import com.compartetutiempo.backend.model.*;
import com.compartetutiempo.backend.model.enums.EstadoProducto;
import com.compartetutiempo.backend.model.enums.EstadoProductoUsuario;
import com.compartetutiempo.backend.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoUsuarioServiceTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private ProductoUsuarioRepository productoUsuarioRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ConversacionRepository conversacionRepository;

    @InjectMocks
    private ProductoUsuarioService productoUsuarioService;

    private Usuario propietario;
    private Usuario comprador;
    private Producto producto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        propietario = new Usuario();
        propietario.setId(1L);
        propietario.setCorreo("propietario@test.com");
        propietario.setNumeroHoras(10.0);

        comprador = new Usuario();
        comprador.setId(2L);
        comprador.setCorreo("comprador@test.com");
        comprador.setNumeroHoras(20.0);

        producto = new Producto();
        producto.setId(100);
        producto.setNombre("Curso Java");
        producto.setNumeroHoras(5);
        producto.setEstado(EstadoProducto.DISPONIBLE);
        producto.setPropietario(propietario);
    }

    @Test
    void adquirirProducto_OK() {
        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
        when(productoUsuarioRepository.findByProductoPropietarioAndComprador(propietario, comprador))
                .thenReturn(Optional.empty());
        when(conversacionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(productoUsuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProductoUsuario transaccion = productoUsuarioService.adquirirProducto(100L, comprador);

        assertNotNull(transaccion);
        assertEquals(EstadoProductoUsuario.PENDIENTE, transaccion.getEstado());
        assertEquals(comprador, transaccion.getComprador());
        verify(conversacionRepository).save(any(Conversacion.class));
        verify(productoUsuarioRepository).save(any(ProductoUsuario.class));
    }

    @Test
    void adquirirProducto_PropietarioNoPuedeComprar() {
        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, 
            () -> productoUsuarioService.adquirirProducto(100L, propietario));

        assertEquals("No puedes adquirir un producto que ya es tuyo", ex.getReason());
    }

    @Test
    void finalizarTransaccion_OK() {
        ProductoUsuario transaccion = new ProductoUsuario();
        transaccion.setId(200);
        transaccion.setProducto(producto);
        transaccion.setComprador(comprador);
        transaccion.setEstado(EstadoProductoUsuario.PENDIENTE);

        when(productoUsuarioRepository.findById(200)).thenReturn(Optional.of(transaccion));
        when(productoUsuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProductoUsuario result = productoUsuarioService.finalizarTransaccion(200, propietario);

        assertEquals(EstadoProductoUsuario.FINALIZADA, result.getEstado());
        assertEquals(EstadoProducto.ENTREGADO, producto.getEstado());
        assertEquals(15.0, propietario.getNumeroHoras());
        assertEquals(15.0, comprador.getNumeroHoras());
        verify(usuarioRepository).saveAll(List.of(comprador, propietario));
        verify(productoRepository).save(producto);
    }

    @Test
    void cancelarSolicitud_OK() {
        ProductoUsuario transaccion = new ProductoUsuario();
        transaccion.setId(300);
        transaccion.setProducto(producto);
        transaccion.setComprador(comprador);
        transaccion.setEstado(EstadoProductoUsuario.PENDIENTE);

        when(productoUsuarioRepository.findById(300)).thenReturn(Optional.of(transaccion));

        productoUsuarioService.cancelarSolicitud(300, "propietario@test.com");

        assertEquals(EstadoProducto.DISPONIBLE, producto.getEstado());
        verify(productoUsuarioRepository).delete(transaccion);
        verify(productoRepository).save(producto);
    }

    @Test
    void cancelarSolicitud_SinPermiso() {
        ProductoUsuario transaccion = new ProductoUsuario();
        transaccion.setId(301);
        transaccion.setProducto(producto);
        transaccion.setComprador(comprador);
        transaccion.setEstado(EstadoProductoUsuario.PENDIENTE);

        when(productoUsuarioRepository.findById(301)).thenReturn(Optional.of(transaccion));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> productoUsuarioService.cancelarSolicitud(301, "otro@test.com"));

        assertEquals("No tienes permiso para cancelar esta transacción", ex.getReason());
    }

    @Test
    void obtenerMisTransacciones_OK() {
        ProductoUsuario transaccion = new ProductoUsuario();
        transaccion.setProducto(producto);
        transaccion.setComprador(comprador);
        transaccion.setEstado(EstadoProductoUsuario.PENDIENTE);

        when(usuarioRepository.findByCorreo("propietario@test.com")).thenReturn(Optional.of(propietario));
        when(productoUsuarioRepository.findByProductoPropietarioOrComprador(propietario, propietario))
                .thenReturn(List.of(transaccion));

        List<ProductoUsuarioDTO> result = productoUsuarioService.obtenerMisTransacciones("propietario@test.com");

        assertEquals(1, result.size());
        assertEquals("Curso Java", result.get(0).getProductoNombre());
    }
}


