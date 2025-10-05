package com.compartetutiempo.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.compartetutiempo.backend.dto.IntercambioDTO;
import com.compartetutiempo.backend.model.Categoria;
import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.IntercambioUsuario;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.model.enums.ModalidadServicio;
import com.compartetutiempo.backend.model.enums.TipoIntercambio;
import com.compartetutiempo.backend.repository.CategoriaRepository;
import com.compartetutiempo.backend.repository.IntercambioRepository;
import com.compartetutiempo.backend.repository.IntercambioUsuarioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class IntercambioServiceTest {

    @Mock
    private IntercambioRepository intercambioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private IntercambioUsuarioRepository intercambioUsuarioRepository;

    @InjectMocks
    private IntercambioService intercambioService;

    private Usuario usuario;
    private Intercambio intercambio;
    private IntercambioDTO intercambioDTO;
    private List<Long> categoriasIds;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("test@test.com");
        usuario.setNombre("Test User");

        intercambio = new Intercambio();
        intercambio.setId(1);
        intercambio.setNombre("Test Intercambio");
        intercambio.setDescripcion("Descripción test");
        intercambio.setNumeroHoras(5);
        intercambio.setTipo(TipoIntercambio.OFERTA);
        intercambio.setModalidad(ModalidadServicio.PRESENCIAL);
        intercambio.setUser(usuario);
        intercambio.setEstado(EstadoIntercambio.EMPAREJAMIENTO);

        categoriasIds = Arrays.asList(1L, 2L);
        
        intercambioDTO = new IntercambioDTO();
        intercambioDTO.setNombre("Test Intercambio");
        intercambioDTO.setDescripcion("Descripción test");
        intercambioDTO.setNumeroHoras(5);
        intercambioDTO.setTipo(TipoIntercambio.OFERTA);
        intercambioDTO.setModalidad(ModalidadServicio.PRESENCIAL);
        intercambioDTO.setCategorias(categoriasIds);
    }

    // PRUEBAS POSITIVAS
    @Test
    void crearIntercambio_ConDatosValidos_DeberiaCrearIntercambio() {
        // Arrange
        when(usuarioRepository.findByCorreo("test@test.com")).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findAllById(categoriasIds)).thenReturn(Arrays.asList(new Categoria(), new Categoria()));
        when(intercambioRepository.save(any(Intercambio.class))).thenReturn(intercambio);

        // Act
        Intercambio resultado = intercambioService.crear("test@test.com", intercambioDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("Test Intercambio", resultado.getNombre());
        assertEquals(EstadoIntercambio.EMPAREJAMIENTO, resultado.getEstado());
        verify(intercambioRepository).save(any(Intercambio.class));
    }

    @Test
    void obtenerPorId_ConIdExistente_DeberiaRetornarIntercambio() {
        // Arrange
        when(intercambioRepository.findById(1)).thenReturn(Optional.of(intercambio));
        when(intercambioUsuarioRepository.findByIntercambioId(1)).thenReturn(Collections.emptyList());

        // Act
        IntercambioDTO resultado = intercambioService.obtenerPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals("Test Intercambio", resultado.getNombre());
    }

    @Test
    void solicitarIntercambio_ConDatosValidos_DeberiaCrearSolicitud() {
        // Arrange
        Usuario demandante = new Usuario();
        demandante.setId(2L);
        demandante.setCorreo("demandante@test.com");

        when(usuarioRepository.findByCorreo("demandante@test.com")).thenReturn(Optional.of(demandante));
        when(intercambioRepository.findById(1)).thenReturn(Optional.of(intercambio));
        when(intercambioUsuarioRepository.findActivosByIntercambioAndUsuario(1, 2L))
            .thenReturn(Collections.emptyList());
        when(intercambioUsuarioRepository.save(any(IntercambioUsuario.class))).thenReturn(new IntercambioUsuario());
        when(intercambioUsuarioRepository.findByIntercambioId(1)).thenReturn(Collections.emptyList());

        // Act
        IntercambioDTO resultado = intercambioService.solicitarIntercambio(1, "demandante@test.com");

        // Assert
        assertNotNull(resultado);
        verify(intercambioUsuarioRepository).save(any(IntercambioUsuario.class));
    }

    // PRUEBAS NEGATIVAS
    @Test
    void crearIntercambio_ConUsuarioNoExistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.findByCorreo("inexistente@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            intercambioService.crear("inexistente@test.com", intercambioDTO));
    }

    @Test
    void solicitarIntercambio_ConUsuarioDueño_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.findByCorreo("test@test.com")).thenReturn(Optional.of(usuario));
        when(intercambioRepository.findById(1)).thenReturn(Optional.of(intercambio));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            intercambioService.solicitarIntercambio(1, "test@test.com"));
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("dueño no puede solicitar intercambio"));
    }

    @Test
    void solicitarIntercambio_ConSolicitudActivaExistente_DeberiaLanzarExcepcion() {
        // Arrange
        Usuario demandante = new Usuario();
        demandante.setId(2L);
        demandante.setCorreo("demandante@test.com");

        when(usuarioRepository.findByCorreo("demandante@test.com")).thenReturn(Optional.of(demandante));
        when(intercambioRepository.findById(1)).thenReturn(Optional.of(intercambio));
        when(intercambioUsuarioRepository.findActivosByIntercambioAndUsuario(1, 2L))
            .thenReturn(Arrays.asList(new IntercambioUsuario()));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            intercambioService.solicitarIntercambio(1, "demandante@test.com"));
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Ya tienes una solicitud"));
    }

    @Test
    void obtenerPorId_ConIdNoExistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(intercambioRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            intercambioService.obtenerPorId(999));
    }

    @Test
    void eliminarIntercambio_ConIdNoExistente_DeberiaLanzarExcepcion() {

        
        assertThrows(RuntimeException.class, () -> 
            intercambioService.eliminarIntercambio(999));
    }
}
