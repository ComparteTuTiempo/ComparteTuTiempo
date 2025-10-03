package com.compartetutiempo.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.compartetutiempo.backend.dto.ParticipacionDTO;
import com.compartetutiempo.backend.dto.EventoResponse;
import com.compartetutiempo.backend.model.Evento;
import com.compartetutiempo.backend.model.Participacion;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoEvento;
import com.compartetutiempo.backend.repository.EventoRepository;
import com.compartetutiempo.backend.repository.ParticipacionRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ParticipacionRepository participacionRepository;

    @InjectMocks
    private EventoService eventoService;

    private Usuario usuario;
    private Evento evento;
    private Participacion participacion;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setCorreo("user@test.com");
        usuario.setNombre("Usuario Test");
        usuario.setNumeroHoras(10.0);

        evento = new Evento();
        evento.setId(1);
        evento.setNombre("Evento Test");
        evento.setDuracion(2.0);
        evento.setFechaEvento(LocalDateTime.now().minusDays(1)); // pasado para finalizar
        evento.setOrganizador(usuario);
        evento.setEstadoEvento(EstadoEvento.DISPONIBLE);

        participacion = new Participacion();
        participacion.setEvento(evento);
        participacion.setUsuario(usuario);
        participacion.setAsistio(false);
    }

    // -------------------------------
    // CREAR EVENTO
    // -------------------------------
    @Test
    void crearEvento_ConOrganizadorValido_DeberiaCrearEvento() {
        when(usuarioRepository.findByCorreo("user@test.com")).thenReturn(Optional.of(usuario));
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        Evento resultado = eventoService.crearEvento(new Evento(), "user@test.com");

        assertNotNull(resultado);
        assertEquals(usuario.getCorreo(), resultado.getOrganizador().getCorreo());
        verify(eventoRepository).save(any(Evento.class));
    }

    @Test
    void crearEvento_ConOrganizadorInexistente_DeberiaLanzarExcepcion() {
        when(usuarioRepository.findByCorreo("inexistente@test.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                eventoService.crearEvento(new Evento(), "inexistente@test.com"));

        assertEquals("Organizador no encontrado", ex.getMessage());
    }

    // -------------------------------
    // PARTICIPAR EN EVENTO
    // -------------------------------
    @Test
    void participarEnEvento_ConUsuarioValido_DeberiaRegistrarParticipacion() {
        Usuario participante = new Usuario();
        participante.setCorreo("participante@test.com");

        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(usuarioRepository.findByCorreo("participante@test.com")).thenReturn(Optional.of(participante));
        when(participacionRepository.findByEventoId(1)).thenReturn(new ArrayList<>());
        when(participacionRepository.save(any(Participacion.class))).thenReturn(new Participacion());

        Participacion resultado = eventoService.participarEnEvento(1, "participante@test.com");

        assertNotNull(resultado);
        verify(participacionRepository).save(any(Participacion.class));
    }

    @Test
    void participarEnEvento_YaInscrito_DeberiaLanzarExcepcion() {
        Participacion p = new Participacion();
        Usuario participante = new Usuario();
        participante.setCorreo("participante@test.com");
        p.setUsuario(participante);

        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(usuarioRepository.findByCorreo("participante@test.com")).thenReturn(Optional.of(participante));
        when(participacionRepository.findByEventoId(1)).thenReturn(List.of(p));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                eventoService.participarEnEvento(1, "participante@test.com"));

        assertEquals("El usuario ya está inscrito en este evento", ex.getMessage());
    }

    @Test
    void participarEnEvento_SiendoOrganizador_DeberiaLanzarExcepcion() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(usuarioRepository.findByCorreo("user@test.com")).thenReturn(Optional.of(usuario));
        when(participacionRepository.findByEventoId(1)).thenReturn(new ArrayList<>());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                eventoService.participarEnEvento(1, "user@test.com"));

        assertEquals("El anfitrión no puede inscribirse en su propio evento", ex.getMessage());
    }

    // -------------------------------
    // FINALIZAR EVENTO
    // -------------------------------
    @Test
    void finalizarEvento_OK_DeberiaFinalizarEventoYSumarHoras() {
        Participacion p = new Participacion();
        Usuario participante = new Usuario();
        participante.setCorreo("participante@test.com");
        participante.setNumeroHoras(5.0);
        p.setUsuario(participante);
        p.setAsistio(true);

        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(participacionRepository.findByEventoId(1)).thenReturn(List.of(p));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(participante);
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        EventoResponse resultado = eventoService.finalizarEvento(1, "user@test.com");

        assertEquals(EstadoEvento.FINALIZADO, evento.getEstadoEvento());
        assertEquals(7.0, participante.getNumeroHoras());
        assertNotNull(resultado);
    }

    @Test
    void finalizarEvento_YaFinalizado_DeberiaLanzarExcepcion() {
        evento.setEstadoEvento(EstadoEvento.FINALIZADO);
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                eventoService.finalizarEvento(1, "user@test.com"));

        assertEquals("El evento ya fue finalizado", ex.getMessage());
    }

    @Test
    void finalizarEvento_SinPermiso_DeberiaLanzarExcepcion() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                eventoService.finalizarEvento(1, "otro@test.com"));

        assertEquals("Solo el organizador puede finalizar el evento", ex.getMessage());
    }

    @Test
    void finalizarEvento_FechaFutura_DeberiaLanzarExcepcion() {
        evento.setFechaEvento(LocalDateTime.now().plusDays(1));
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                eventoService.finalizarEvento(1, "user@test.com"));

        assertEquals("No puedes finalizar un evento que aún no ha ocurrido", ex.getMessage());
    }

    // -------------------------------
    // MARCAR ASISTENCIA
    // -------------------------------
    @Test
    void marcarAsistencia_SiendoOrganizador_DeberiaActualizar() {
        Usuario participante = new Usuario();
        participante.setCorreo("participante@test.com");

        Participacion p = new Participacion();
        p.setUsuario(participante);
        p.setAsistio(false);

        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(participacionRepository.findByEventoIdAndUsuarioCorreo(1, "participante@test.com"))
            .thenReturn(Optional.of(p));
        when(participacionRepository.save(any(Participacion.class))).thenReturn(p);

        eventoService.marcarAsistencia(1, "user@test.com", "participante@test.com", true);

        assertTrue(p.isAsistio());
        verify(participacionRepository).save(p);
    }

    @Test
    void marcarAsistencia_SinSerOrganizador_DeberiaLanzarExcepcion() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                eventoService.marcarAsistencia(1, "otro@test.com", "participante@test.com", true));

        assertEquals("Solo el organizador puede marcar asistencia", ex.getMessage());
    }

    // -------------------------------
    // OBTENER PARTICIPACIONES
    // -------------------------------
    @Test
    void obtenerParticipacionesEvento_OK() {
        Usuario participante = new Usuario();
        participante.setCorreo("participante@test.com");
        participacion.setUsuario(participante);

        when(participacionRepository.findByEventoId(1)).thenReturn(List.of(participacion));

        List<ParticipacionDTO> resultado = eventoService.obtenerParticipacionesEvento(1);

        assertEquals(1, resultado.size());
        assertEquals("participante@test.com", resultado.get(0).getCorreo());
    }
}

