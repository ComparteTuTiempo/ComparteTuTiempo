package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.dto.ProductoDTO;
import com.compartetutiempo.backend.model.*;
import com.compartetutiempo.backend.model.enums.EstadoProducto;
import com.compartetutiempo.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoServiceTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ConversacionRepository conversacionRepository;
    @Mock private ProductoUsuarioRepository productoUsuarioRepository;

    @InjectMocks private ProductoService productoService;

    private Usuario comprador;
    private Usuario propietario;
    private Producto producto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        comprador = new Usuario();
        comprador.setId(1L);
        comprador.setCorreo("comprador@mail.com");
        comprador.setNumeroHoras(10);

        propietario = new Usuario();
        propietario.setId(2L);
        propietario.setCorreo("propietario@mail.com");
        propietario.setNumeroHoras(5);

        producto = new Producto();
        producto.setId(100);
        producto.setNombre("Libro de Java");
        producto.setNumeroHoras(3);
        producto.setEstado(EstadoProducto.DISPONIBLE);
        producto.setPropietario(propietario);
    }


    @Test
    void solicitarProducto_OK() {
        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
        when(usuarioRepository.findByCorreo("comprador@mail.com")).thenReturn(Optional.of(comprador));
        when(conversacionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(productoUsuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(productoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProductoDTO dto = productoService.solicitarProducto(100L, "comprador@mail.com");

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(producto.getEstado()).isEqualTo(EstadoProducto.RESERVADO);
        verify(conversacionRepository).save(any());
        verify(productoUsuarioRepository).save(any());
        verify(productoRepository).save(producto);
    }

    @Test
    void solicitarProducto_Propietario_Throws() {
        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
        when(usuarioRepository.findByCorreo("propietario@mail.com")).thenReturn(Optional.of(propietario));

        assertThatThrownBy(() -> productoService.solicitarProducto(100L, "propietario@mail.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No puedes reservar tu propio producto");
    }

    @Test
    void solicitarProducto_NoDisponible_Throws() {
        producto.setEstado(EstadoProducto.ENTREGADO);
        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
        when(usuarioRepository.findByCorreo("comprador@mail.com")).thenReturn(Optional.of(comprador));

        assertThatThrownBy(() -> productoService.solicitarProducto(100L, "comprador@mail.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("no está disponible");
    }


    @Test
    void actualizarProducto_OK() {
        Producto modificado = new Producto();
        modificado.setNombre("Nuevo nombre");
        modificado.setDescripcion("Desc");
        modificado.setNumeroHoras(5);
        modificado.setEstado(EstadoProducto.DISPONIBLE);

        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
        ProductoDTO dto = productoService.actualizarProducto(100L, modificado, propietario);

        assertThat(dto.getNombre()).isEqualTo("Nuevo nombre");
        verify(productoRepository).save(producto);
    }

    @Test
    void actualizarProducto_NoPermiso_Throws() {
        Usuario otro = new Usuario();
        otro.setId(99L);

        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
        assertThatThrownBy(() -> productoService.actualizarProducto(100L, new Producto(), otro))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No tienes permisos");
    }


    @Test
    void eliminarProducto_OK() {
        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
        productoService.eliminarProducto(100L, propietario);
        verify(productoRepository).delete(producto);
    }

    @Test
    void eliminarProducto_NoPermiso_Throws() {
        Usuario otro = new Usuario();
        otro.setId(99L);
        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> productoService.eliminarProducto(100L, otro))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No tienes permisos");
    }
}

