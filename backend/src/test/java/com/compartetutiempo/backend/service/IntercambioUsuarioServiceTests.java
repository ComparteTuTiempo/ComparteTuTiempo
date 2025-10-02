package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.dto.AcuerdoRequest;
import com.compartetutiempo.backend.dto.IntercambioDTO;
import com.compartetutiempo.backend.dto.IntercambioUsuarioDTO;
import com.compartetutiempo.backend.model.*;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.model.enums.TipoIntercambio;
import com.compartetutiempo.backend.model.enums.TipoNotificacion;
import com.compartetutiempo.backend.repository.IntercambioUsuarioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IntercambioUsuarioServiceTest {

    @Mock
    private IntercambioUsuarioRepository intercambioUsuarioRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ConversacionService conversacionService;
    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private IntercambioUsuarioService intercambioUsuarioService;

    private Usuario ofertante;
    private Usuario solicitante;
    private Intercambio intercambio;
    private IntercambioUsuario intercambioUsuario;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        ofertante = new Usuario();
        ofertante.setId(1l);
        ofertante.setCorreo("ofertante@mail.com");
        ofertante.setNumeroHoras(10.0);

        solicitante = new Usuario();
        solicitante.setId(2L);
        solicitante.setCorreo("solicitante@mail.com");
        solicitante.setNumeroHoras(5.0);

        intercambio = new Intercambio();
        intercambio.setId(100);
        intercambio.setUser(ofertante);
        intercambio.setNombre("Clases de inglés");
        intercambio.setEstado(EstadoIntercambio.EMPAREJAMIENTO);
        intercambio.setTipo(TipoIntercambio.OFERTA);

        intercambioUsuario = new IntercambioUsuario();
        intercambioUsuario.setId(200);
        intercambioUsuario.setUsuario(solicitante);
        intercambioUsuario.setIntercambio(intercambio);
        intercambioUsuario.setEstado(EstadoIntercambio.EMPAREJAMIENTO);
    }

    @Test
    void aceptarSolicitud_OK() {
        // Arrange
        when(intercambioUsuarioRepository.findById(200)).thenReturn(Optional.of(intercambioUsuario));
        when(intercambioUsuarioRepository.findByIntercambioId(100)).thenReturn(java.util.List.of(intercambioUsuario));

        // Act
        IntercambioDTO result = intercambioUsuarioService.aceptarSolicitud(200, "ofertante@mail.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(intercambioUsuario.getEstado()).isEqualTo(EstadoIntercambio.CONSENSO);
        verify(conversacionService).getOrCreateForIntercambioUsuario(eq(200), anyList());
        verify(notificacionService).crearYEnviar(eq(solicitante), eq(TipoNotificacion.INTERCAMBIO), contains("aceptada"), isNull());
        verify(intercambioUsuarioRepository).save(intercambioUsuario);
    }

    @Test
    void aceptarSolicitud_NotOwner_Throws() {
        when(intercambioUsuarioRepository.findById(200)).thenReturn(Optional.of(intercambioUsuario));

        assertThatThrownBy(() -> intercambioUsuarioService.aceptarSolicitud(200, "otro@mail.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No tienes permiso");
    }

    @Test
    void establecerAcuerdo_OK() {
        AcuerdoRequest request = new AcuerdoRequest();
        request.setHorasAsignadas(3.0);
        request.setTerminos("Condiciones del intercambio");

        when(intercambioUsuarioRepository.findById(200)).thenReturn(Optional.of(intercambioUsuario));

        IntercambioUsuarioDTO result = intercambioUsuarioService.establecerAcuerdo(200, request, "solicitante@mail.com");

        assertThat(result).isNotNull();
        assertThat(intercambioUsuario.getEstado()).isEqualTo(EstadoIntercambio.EJECUCION);
        verify(notificacionService).crearYEnviar(eq(solicitante), eq(TipoNotificacion.INTERCAMBIO), contains("ejecución"), isNull());
        verify(intercambioUsuarioRepository).save(intercambioUsuario);
    }

    @Test
    void establecerAcuerdo_NoHoras_Throws() {
        AcuerdoRequest request = new AcuerdoRequest();
        request.setHorasAsignadas(10.0);

        when(intercambioUsuarioRepository.findById(200)).thenReturn(Optional.of(intercambioUsuario));

        assertThatThrownBy(() -> intercambioUsuarioService.establecerAcuerdo(200, request, "solicitante@mail.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("no dispone de suficientes horas");
    }

    @Test
    void finalizarAcuerdo_OK() {
        intercambioUsuario.setEstado(EstadoIntercambio.EJECUCION);
        intercambioUsuario.setHorasAsignadas(2.0);

        when(intercambioUsuarioRepository.findById(200)).thenReturn(Optional.of(intercambioUsuario));

        IntercambioUsuarioDTO result = intercambioUsuarioService.finalizarAcuerdo(200, "ofertante@mail.com");

        assertThat(result).isNotNull();
        assertThat(intercambioUsuario.getEstado()).isEqualTo(EstadoIntercambio.FINALIZADO);
        verify(usuarioRepository).saveAll(anyList());
        verify(notificacionService).crearYEnviar(eq(solicitante), eq(TipoNotificacion.INTERCAMBIO), contains("finalizado"), isNull());
    }

    @Test
    void rechazarSolicitud_OK() {
        when(intercambioUsuarioRepository.findById(200)).thenReturn(Optional.of(intercambioUsuario));

        intercambioUsuarioService.rechazarSolicitud(200, "ofertante@mail.com");

        verify(notificacionService).crearYEnviar(eq(solicitante), eq(TipoNotificacion.INTERCAMBIO), contains("denegado"), isNull());
        verify(intercambioUsuarioRepository).delete(intercambioUsuario);
    }

    @Test
    void aceptarSolicitud_yaEnConsenso_Throws() {
        intercambioUsuario.setEstado(EstadoIntercambio.CONSENSO);
        when(intercambioUsuarioRepository.findById(200)).thenReturn(Optional.of(intercambioUsuario));

        assertThatThrownBy(() -> intercambioUsuarioService.aceptarSolicitud(200, "ofertante@mail.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ya está en consenso");
    }

    @Test
    void establecerAcuerdo_NoPermiso_Throws() {
        AcuerdoRequest request = new AcuerdoRequest();
        request.setHorasAsignadas(2.0);

        when(intercambioUsuarioRepository.findById(200)).thenReturn(Optional.of(intercambioUsuario));

        assertThatThrownBy(() -> intercambioUsuarioService.establecerAcuerdo(200, request, "otro@mail.com"))
                .isInstanceOf(IllegalAccessError.class)
                .hasMessageContaining("No tienes permiso para establecer este acuerdo");
    }
    
    @Test
    void finalizarAcuerdo_NoPermiso_Throws() {
        intercambioUsuario.setEstado(EstadoIntercambio.EJECUCION);
        when(intercambioUsuarioRepository.findById(200)).thenReturn(Optional.of(intercambioUsuario));

        assertThatThrownBy(() -> intercambioUsuarioService.finalizarAcuerdo(200, "otro@mail.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No tienes permiso para finalizar este acuerdo");
    }

    @Test
    void finalizarAcuerdo_NoEjecucion_Throws() {
        intercambioUsuario.setEstado(EstadoIntercambio.CONSENSO); // no en ejecución
        when(intercambioUsuarioRepository.findById(200)).thenReturn(Optional.of(intercambioUsuario));

        assertThatThrownBy(() -> intercambioUsuarioService.finalizarAcuerdo(200, "ofertante@mail.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("El acuerdo no está en ejecución");
    }

    @Test
    void rechazarSolicitud_SinPermiso_Throws() {
        when(intercambioUsuarioRepository.findById(200)).thenReturn(Optional.of(intercambioUsuario));

        assertThatThrownBy(() -> intercambioUsuarioService.rechazarSolicitud(200, "otro@mail.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No tienes permiso para rechazar esta solicitud");
    }
}

