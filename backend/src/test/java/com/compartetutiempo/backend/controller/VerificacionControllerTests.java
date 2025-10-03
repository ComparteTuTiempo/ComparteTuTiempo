package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.config.SecurityTestConfig;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.Verificacion;
import com.compartetutiempo.backend.model.enums.EstadoVerificacion;
import com.compartetutiempo.backend.service.StorageService;
import com.compartetutiempo.backend.service.UsuarioService;
import com.compartetutiempo.backend.service.VerificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VerificacionController.class)
@Import({SecurityTestConfig.class, VerificacionControllerTests.MockConfig.class})
class VerificacionControllerTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private VerificacionService verificacionService;
    @Autowired private UsuarioService usuarioService;

    private Usuario usuario;
    private Verificacion verificacion;

    // JWT simulando usuario normal
    private final org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser =
            jwt().jwt(jwt -> jwt.subject("user@test.com"));

    // JWT simulando admin
    private final org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAdmin =
            jwt().jwt(jwt -> jwt.subject("admin@test.com")
                    .claim("roles", List.of("ROLE_ADMIN")));

    @TestConfiguration
    static class MockConfig {
        @Bean VerificacionService verificacionService() { return Mockito.mock(VerificacionService.class); }
        @Bean UsuarioService usuarioService() { return Mockito.mock(UsuarioService.class); }
        @Bean StorageService storageService() { return Mockito.mock(StorageService.class); }
    }

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setCorreo("user@test.com");
        usuario.setVerificado(false);

        verificacion = new Verificacion();
        verificacion.setId(1L);
        verificacion.setDocumentoURL("doc.png");
        verificacion.setEstado(EstadoVerificacion.PENDIENTE);
        verificacion.setUsuario(usuario);
    }

    @Test
    @DisplayName("POST /api/verificaciones/{correo} - crear verificación OK")
    void crearVerificacion_OK() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "documentoURL",
                "doc.png",
                "image/png",
                "contenido-fake".getBytes()
        );

        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuario);
        when(verificacionService.crear(any(Verificacion.class))).thenReturn(verificacion);

        mockMvc.perform(multipart("/api/verificaciones/user@test.com")
                        .file(file)
                        .with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentoURL").value("doc.png"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("POST /api/verificaciones/{correo} - usuario no encontrado")
    void crearVerificacion_UsuarioNoEncontrado() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "documentoURL", "doc.png", "image/png", "contenido-fake".getBytes()
        );

        when(usuarioService.obtenerPorCorreo("otro@test.com")).thenReturn(null);

        mockMvc.perform(multipart("/api/verificaciones/otro@test.com")
                        .file(file)
                        .with(jwtUser))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("GET /api/verificaciones/pendientes - admin OK")
    void listarPendientes_OK() throws Exception {
        when(verificacionService.obtenerPendientes()).thenReturn(List.of(verificacion));

        mockMvc.perform(get("/api/verificaciones/pendientes").with(jwtAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentoURL").value("doc.png"));
    }

    @Test
    @DisplayName("PUT /api/verificaciones/{id}/aprobar - admin OK")
    void aprobar_OK() throws Exception {
        verificacion.setEstado(EstadoVerificacion.APROBADA);

        when(verificacionService.obtenerPorId(1L)).thenReturn(verificacion);
        when(verificacionService.aprobar(any(Verificacion.class))).thenReturn(verificacion);

        mockMvc.perform(put("/api/verificaciones/1/aprobar").with(jwtAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADA"));

        verify(usuarioService).guardar(usuario);
    }


    @Test
    @DisplayName("PUT /api/verificaciones/{id}/rechazar - admin OK")
    void rechazar_OK() throws Exception {
        verificacion.setEstado(EstadoVerificacion.RECHAZADA);

        when(verificacionService.obtenerPorId(1L)).thenReturn(verificacion);
        when(verificacionService.rechazar(any(Verificacion.class))).thenReturn(verificacion);

        mockMvc.perform(put("/api/verificaciones/1/rechazar").with(jwtAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADA"));
    }
}
