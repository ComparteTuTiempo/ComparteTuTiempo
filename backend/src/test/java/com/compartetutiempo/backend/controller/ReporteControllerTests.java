package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.config.SecurityTestConfig;
import com.compartetutiempo.backend.model.Reporte;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoReporte;
import com.compartetutiempo.backend.service.ReporteService;
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

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReporteController.class)
@Import({SecurityTestConfig.class, ReporteControllerTest.MockConfig.class})
class ReporteControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ReporteService reporteService;
    @Autowired private ObjectMapper objectMapper;

    private Reporte reporte;

    private final org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser =
            jwt().jwt(jwt -> jwt.subject("reportador@test.com"));

    @TestConfiguration
    static class MockConfig {
        @Bean ReporteService reporteService() { return Mockito.mock(ReporteService.class); }
        @Bean UsuarioService usuarioService() { return Mockito.mock(UsuarioService.class); }
    }

    @BeforeEach
    void setUp() {
        Usuario reportador = new Usuario();
        reportador.setId(1L);
        reportador.setCorreo("reportador@test.com");

        Usuario reportado = new Usuario();
        reportado.setId(2L);
        reportado.setCorreo("reportado@test.com");

        reporte = new Reporte();
        reporte.setId(100);
        reporte.setTitulo("Spam");
        reporte.setDescripcion("Hace spam en los intercambios");
        reporte.setUsuarioReportador(reportador);
        reporte.setUsuarioReportado(reportado);
        reporte.setEstado(EstadoReporte.PENDIENTE);
        reporte.setFechaCreacion(new Date());
    }

    // -------------------------------
    // CREAR REPORTE
    // -------------------------------
    @Test
    @DisplayName("POST /api/reportes/{correoReportado} - crear reporte OK")
    void crearReporte_OK() throws Exception {
        when(reporteService.crearReporte(eq("reportador@test.com"), eq("reportado@test.com"), any(Reporte.class)))
                .thenReturn(reporte);

        mockMvc.perform(post("/api/reportes/reportado@test.com")
                        .with(jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reporte)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Spam"));
    }

    // -------------------------------
    // LISTAR TODOS
    // -------------------------------
    @Test
    @DisplayName("GET /api/reportes - listar todos OK")
    void obtenerTodos_OK() throws Exception {
        when(reporteService.obtenerTodos()).thenReturn(List.of(reporte));

        mockMvc.perform(get("/api/reportes").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100));
    }

    // -------------------------------
    // LISTAR PENDIENTES
    // -------------------------------
    @Test
    @DisplayName("GET /api/reportes/pendientes - listar pendientes OK")
    void obtenerPendientes_OK() throws Exception {
        when(reporteService.obtenerPendientes()).thenReturn(List.of(reporte));

        mockMvc.perform(get("/api/reportes/pendientes").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    // -------------------------------
    // OBTENER POR ID
    // -------------------------------
    @Test
    @DisplayName("GET /api/reportes/{id} - obtener reporte OK")
    void obtenerReporte_OK() throws Exception {
        when(reporteService.obtenerPorId(100L)).thenReturn(reporte);

        mockMvc.perform(get("/api/reportes/100").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Spam"));
    }

    @Test
    @DisplayName("GET /api/reportes/{id} - no encontrado")
    void obtenerReporte_NoEncontrado() throws Exception {
        when(reporteService.obtenerPorId(999L)).thenThrow(new RuntimeException("Reporte no encontrado"));

        mockMvc.perform(get("/api/reportes/999").with(jwtUser))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------
    // CONFIRMAR REPORTE
    // -------------------------------
    @Test
    @DisplayName("POST /api/reportes/{id}/confirmar - OK")
    void confirmarReporte_OK() throws Exception {
        reporte.setEstado(EstadoReporte.CONFIRMADO);
        when(reporteService.confirmarReporte(100L)).thenReturn(reporte);

        mockMvc.perform(post("/api/reportes/100/confirmar").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    // -------------------------------
    // RECHAZAR REPORTE
    // -------------------------------
    @Test
    @DisplayName("POST /api/reportes/{id}/rechazar - OK")
    void rechazarReporte_OK() throws Exception {
        reporte.setEstado(EstadoReporte.RECHAZADO);
        when(reporteService.rechazarReporte(100L)).thenReturn(reporte);

        mockMvc.perform(post("/api/reportes/100/rechazar").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADO"));
    }
}

