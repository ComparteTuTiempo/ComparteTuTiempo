package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.config.SecurityTestConfig;
import com.compartetutiempo.backend.dto.EventoRequest;
import com.compartetutiempo.backend.dto.EventoResponse;
import com.compartetutiempo.backend.dto.ParticipacionDTO;
import com.compartetutiempo.backend.dto.UsuarioDTO;
import com.compartetutiempo.backend.model.Evento;
import com.compartetutiempo.backend.model.Participacion;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoEvento;
import com.compartetutiempo.backend.service.EventoService;
import com.compartetutiempo.backend.service.NotificacionService;
import com.compartetutiempo.backend.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EventoController.class)
@Import({SecurityTestConfig.class, EventoControllerTest.MockConfig.class})
class EventoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EventoService eventoService;
    @Autowired private ObjectMapper objectMapper;

    private EventoResponse eventoResponse;
    private Participacion participacion;

    // JWT para simular usuario autenticado
    private final org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser =
            jwt().jwt(jwt -> jwt.subject("user@test.com"));

    @TestConfiguration
    static class MockConfig {
        @Bean EventoService eventoService() { return Mockito.mock(EventoService.class); }
        @Bean UsuarioService usuarioService() { return Mockito.mock(UsuarioService.class); }
        @Bean NotificacionService notificacionService() { return Mockito.mock(NotificacionService.class); }
    }

    @BeforeEach
    void setUp() {
        UsuarioDTO organizadorDTO = new UsuarioDTO(
            null, "User", "user@test.com", null, null, false, true, null, null
        );
        eventoResponse = new EventoResponse(
            1, "Evento Test", "Descripción", "Madrid", 2,
            LocalDateTime.now().plusDays(1), EstadoEvento.DISPONIBLE,
            organizadorDTO
        );

        Evento evento = new Evento();
        evento.setId(1);
        Usuario organizador = new Usuario();
        organizador.setCorreo("user@test.com");
        evento.setOrganizador(organizador);

        participacion = new Participacion();
        participacion.setEvento(evento);
    }


    // -------------------------------
    // CREAR EVENTO
    // -------------------------------
    @Test
    @DisplayName("POST /eventos/crear - crear evento OK")
    void crearEvento_OK() throws Exception {
        EventoRequest request = new EventoRequest();
        request.setNombre("Evento Test");
        request.setDescripcion("Descripción");
        request.setUbicacion("Madrid");
        request.setDuracion(2);
        request.setFechaEvento(LocalDateTime.now().plusDays(1));
        request.setCorreoOrganizador("org@test.com");

        when(eventoService.crearEvento(any(), eq("org@test.com")))
                .thenReturn(new com.compartetutiempo.backend.model.Evento());

        mockMvc.perform(post("/eventos/crear")
                        .with(jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /eventos/crear - error al crear")
    void crearEvento_Error() throws Exception {
        EventoRequest request = new EventoRequest();
        request.setNombre("Evento Test");
        request.setDescripcion("Descripción");
        request.setUbicacion("Madrid");
        request.setDuracion(2);
        request.setFechaEvento(LocalDateTime.now().plusDays(1));
        request.setCorreoOrganizador("org@test.com");

        when(eventoService.crearEvento(any(), eq("org@test.com")))
                .thenThrow(new RuntimeException("Organizador no encontrado"));

        mockMvc.perform(post("/eventos/crear")
                        .with(jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------
    // LISTAR EVENTOS
    // -------------------------------
    @Test
    @DisplayName("GET /eventos - listar eventos OK")
    void listarEventos_OK() throws Exception {
        when(eventoService.listarEventos()).thenReturn(List.of(eventoResponse));

        mockMvc.perform(get("/eventos").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Evento Test"));
    }

    // -------------------------------
    // OBTENER EVENTO POR ID
    // -------------------------------
    @Test
    @DisplayName("GET /eventos/{id} - obtener evento OK")
    void obtenerEvento_OK() throws Exception {
        when(eventoService.obtenerEventoPorId(1)).thenReturn(eventoResponse);

        mockMvc.perform(get("/eventos/1").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Evento Test"));
    }

    @Test
    @DisplayName("GET /eventos/{id} - evento no encontrado")
    void obtenerEvento_NoEncontrado() throws Exception {
        when(eventoService.obtenerEventoPorId(99))
                .thenThrow(new RuntimeException("Evento no encontrado"));

        mockMvc.perform(get("/eventos/99").with(jwtUser))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------
    // PARTICIPAR EN EVENTO
    // -------------------------------
    @Test
    @DisplayName("POST /eventos/{id}/participar/{correo} - participar OK")
    void participarEvento_OK() throws Exception {
        when(eventoService.participarEnEvento(1, "user@test.com"))
                .thenReturn(participacion);

        mockMvc.perform(post("/eventos/1/participar/user@test.com")
                        .with(jwtUser))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /eventos/{id}/participar/{correo} - ya inscrito")
    void participarEvento_yaInscrito() throws Exception {
        when(eventoService.participarEnEvento(1, "user@test.com"))
                .thenThrow(new RuntimeException("El usuario ya está inscrito"));

        mockMvc.perform(post("/eventos/1/participar/user@test.com")
                        .with(jwtUser))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El usuario ya está inscrito"));
    }

    // -------------------------------
    // MARCAR ASISTENCIA
    // -------------------------------
    @Test
    @DisplayName("POST /eventos/{id}/asistencia - asistencia OK")
    void marcarAsistencia_OK() throws Exception {
        mockMvc.perform(post("/eventos/1/asistencia")
                        .with(jwtUser)
                        .param("correoOrganizador", "org@test.com")
                        .param("correoParticipante", "user@test.com")
                        .param("asistio", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Asistencia marcada correctamente"));
    }

    @Test
    @DisplayName("POST /eventos/{id}/asistencia - error sin permiso")
    void marcarAsistencia_Error() throws Exception {
        Mockito.doThrow(new RuntimeException("Solo el organizador puede marcar asistencia"))
                .when(eventoService).marcarAsistencia(anyInt(), anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/eventos/1/asistencia")
                        .with(jwtUser)
                        .param("correoOrganizador", "otro@test.com")
                        .param("correoParticipante", "user@test.com")
                        .param("asistio", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Solo el organizador puede marcar asistencia"));
    }

    // -------------------------------
    // FINALIZAR EVENTO
    // -------------------------------
    @Test
    @DisplayName("POST /eventos/{id}/finalizar - finalizar OK")
    void finalizarEvento_OK() throws Exception {
        when(eventoService.finalizarEvento(1, "org@test.com")).thenReturn(eventoResponse);
        when(eventoService.obtenerParticipacionesEvento(1))
                .thenReturn(List.of(new ParticipacionDTO("user@test.com", "User", null, true)));

        mockMvc.perform(post("/eventos/1/finalizar")
                        .with(jwtUser)
                        .param("correoOrganizador", "org@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Evento Test"));
    }

    @Test
    @DisplayName("POST /eventos/{id}/finalizar - error ya finalizado")
    void finalizarEvento_Error() throws Exception {
        when(eventoService.finalizarEvento(1, "org@test.com"))
                .thenThrow(new RuntimeException("El evento ya fue finalizado"));

        mockMvc.perform(post("/eventos/1/finalizar")
                        .with(jwtUser)
                        .param("correoOrganizador", "org@test.com"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El evento ya fue finalizado"));
    }

    // -------------------------------
    // LISTA PARTICIPANTES
    // -------------------------------
    @Test
    @DisplayName("GET /eventos/{id}/participantes/lista - lista OK")
    void listaParticipantes_OK() throws Exception {
        when(eventoService.obtenerEventoPorId(1)).thenReturn(eventoResponse);
        when(eventoService.obtenerParticipacionesEvento(1))
                .thenReturn(List.of(new ParticipacionDTO("user@test.com", "User", null, true)));

        mockMvc.perform(get("/eventos/1/participantes/lista")
                        .with(jwtUser)
                        .param("correoOrganizador", "user@test.com"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /eventos/{id}/participantes/lista - sin permisos")
    void listaParticipantes_SinPermiso() throws Exception {
        EventoResponse otro = new EventoResponse(
                1, "Evento Test", "Desc", "Madrid", 2,
                LocalDateTime.now().plusDays(1), EstadoEvento.DISPONIBLE,
                new UsuarioDTO(null, "Otro", "otro@test.com", null, null, false, true, null, null)
        );

        when(eventoService.obtenerEventoPorId(1)).thenReturn(otro);

        mockMvc.perform(get("/eventos/1/participantes/lista")
                        .with(jwtUser)
                        .param("correoOrganizador", "user@test.com"))
                .andExpect(status().isForbidden());
    }
}
