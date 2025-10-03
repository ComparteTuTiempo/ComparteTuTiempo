package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.config.SecurityTestConfig;
import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.ResenaIntercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.service.NotificacionService;
import com.compartetutiempo.backend.service.ResenaIntercambioService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ResenaIntercambioController.class)
@Import({SecurityTestConfig.class, ResenaIntercambioControllerTest.MockConfig.class})
class ResenaIntercambioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResenaIntercambioService resenaService;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario autor;
    private Intercambio intercambio;
    private ResenaIntercambio resena;

    private final org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser =
            jwt().jwt(jwt -> jwt.subject("autor@test.com"));

    @TestConfiguration
    static class MockConfig {
        @Bean
        ResenaIntercambioService resenaService() { return Mockito.mock(ResenaIntercambioService.class); }
        @Bean
        NotificacionService notificacionService() { return Mockito.mock(NotificacionService.class); }
    }

    @BeforeEach
    void setUp() {
        autor = new Usuario();
        autor.setCorreo("autor@test.com");
        autor.setNombre("Autor Test");

        intercambio = new Intercambio();
        intercambio.setId(1);
        intercambio.setNombre("Intercambio Test");
        intercambio.setUser(new Usuario());

        resena = new ResenaIntercambio();
        resena.setPuntuacion(5);
        resena.setComentario("Excelente");
        resena.setAutor(autor);
        resena.setIntercambio(intercambio);
    }

    @Test
    @DisplayName("POST /resenas/intercambios/{intercambioId} - crear reseña OK")
    void crearResena_OK() throws Exception {
        when(resenaService.crear(eq(1), eq("autor@test.com"), any(ResenaIntercambio.class)))
                .thenReturn(resena);

        mockMvc.perform(post("/resenas/intercambios/1")
                        .with(jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resena)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puntuacion").value(5))
                .andExpect(jsonPath("$.comentario").value("Excelente"))
                .andExpect(jsonPath("$.autor.correo").value("autor@test.com"));
    }

    @Test
    @DisplayName("GET /resenas/intercambios/{intercambioId} - listar reseñas OK")
    void listarResenas_OK() throws Exception {
        when(resenaService.obtenerPorIntercambio(1)).thenReturn(List.of(resena));

        mockMvc.perform(get("/resenas/intercambios/1").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].puntuacion").value(5))
                .andExpect(jsonPath("$[0].comentario").value("Excelente"));
    }

    @Test
    @DisplayName("GET /resenas/intercambios/{intercambioId}/promedio - calcular promedio OK")
    void calcularPromedio_OK() throws Exception {
        when(resenaService.calcularPromedio(1)).thenReturn(4.5);

        mockMvc.perform(get("/resenas/intercambios/1/promedio").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));
    }
}
