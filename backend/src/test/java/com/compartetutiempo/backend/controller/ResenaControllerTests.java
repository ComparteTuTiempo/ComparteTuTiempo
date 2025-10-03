package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.config.SecurityTestConfig;
import com.compartetutiempo.backend.model.Resena;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.TipoNotificacion;
import com.compartetutiempo.backend.service.NotificacionService;
import com.compartetutiempo.backend.service.ResenaService;
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

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ResenaController.class)
@Import({SecurityTestConfig.class, ResenaControllerTest.MockConfig.class})
class ResenaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResenaService resenaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario autor;
    private Usuario destinatario;

    private final org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser =
            jwt().jwt(jwt -> jwt.subject("autor@test.com"));

    @TestConfiguration
    static class MockConfig {
        @Bean
        ResenaService resenaService() { return Mockito.mock(ResenaService.class); }
        @Bean
        UsuarioService usuarioService() { return Mockito.mock(UsuarioService.class); }
        @Bean
        NotificacionService notificacionService() { return Mockito.mock(NotificacionService.class); }
    }

    @BeforeEach
    void setUp() {
        autor = new Usuario();
        autor.setCorreo("autor@test.com");
        autor.setNombre("Autor");
        autor.setActivo(true);

        destinatario = new Usuario();
        destinatario.setCorreo("destinatario@test.com");
        destinatario.setNombre("Destinatario");
        destinatario.setActivo(true);
    }

    @Test
    @DisplayName("POST /api/resenas/{autor}/{dest} - crear reseña OK")
    void crearResena_OK() throws Exception {
        Resena resena = new Resena();
        resena.setAutor(autor);
        resena.setDestinatario(destinatario);
        resena.setPuntuacion(5);
        resena.setComentario("Excelente");

        when(resenaService.crearResena(eq("autor@test.com"), eq("destinatario@test.com"), eq(5), eq("Excelente")))
                .thenReturn(resena);
        when(usuarioService.obtenerPorCorreo("autor@test.com")).thenReturn(autor);
        when(usuarioService.obtenerPorCorreo("destinatario@test.com")).thenReturn(destinatario);

        mockMvc.perform(post("/api/resenas/autor@test.com/destinatario@test.com")
                        .with(jwtUser)
                        .param("puntuacion", "5")
                        .param("comentario", "Excelente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puntuacion").value(5))
                .andExpect(jsonPath("$.comentario").value("Excelente"));
    }

    @Test
    @DisplayName("POST /api/resenas/{autor}/{dest} - error mismo usuario")
    void crearResena_ErrorMismoUsuario() throws Exception {
        when(resenaService.crearResena(eq("autor@test.com"), eq("autor@test.com"), eq(5), eq("Comentario")))
                .thenThrow(new IllegalArgumentException("No puedes dejarte reseñas a ti mismo"));

        mockMvc.perform(post("/api/resenas/autor@test.com/autor@test.com")
                        .with(jwtUser)
                        .param("puntuacion", "5")
                        .param("comentario", "Comentario"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No puedes dejarte reseñas a ti mismo"));
    }

    @Test
    @DisplayName("GET /api/resenas/{correo} - obtener reseñas OK")
    void obtenerResenas_OK() throws Exception {
        Resena resena = new Resena();
        resena.setAutor(autor);
        resena.setDestinatario(destinatario);
        resena.setPuntuacion(4);

        when(resenaService.obtenerResenas("destinatario@test.com")).thenReturn(List.of(resena));

        mockMvc.perform(get("/api/resenas/destinatario@test.com").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].puntuacion").value(4));
    }

    @Test
    @DisplayName("GET /api/resenas/{correo}/promedio - calcular promedio OK")
    void calcularPromedio_OK() throws Exception {
        when(resenaService.calcularPromedio("destinatario@test.com")).thenReturn(4.5);

        mockMvc.perform(get("/api/resenas/destinatario@test.com/promedio").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));
    }
}




