package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.config.SecurityTestConfig;
import com.compartetutiempo.backend.model.Categoria;
import com.compartetutiempo.backend.service.CategoriaService;
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

@WebMvcTest(controllers = CategoriaController.class)
@Import({SecurityTestConfig.class, CategoriaControllerTests.MockConfig.class})
class CategoriaControllerTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private CategoriaService categoriaService;
    @Autowired private ObjectMapper objectMapper;

    private Categoria categoria;

    // Simulación de usuario autenticado con JWT
    private final org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser =
            jwt().jwt(jwt -> jwt.subject("user@test.com"));

    @TestConfiguration
    static class MockConfig {
        @Bean CategoriaService categoriaService() { return Mockito.mock(CategoriaService.class); }
    }

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Tecnología");
    }


    @Test
    @DisplayName("POST /categorias - crear categoría OK")
    void crearCategoria_OK() throws Exception {
        when(categoriaService.crearCategoria(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(post("/categorias")
                        .with(jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Tecnología"));
    }


    @Test
    @DisplayName("GET /categorias - listar categorías OK")
    void listarCategorias_OK() throws Exception {
        when(categoriaService.obtenerCategorias()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/categorias").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Tecnología"));
    }

    @Test
    @DisplayName("PUT /categorias/{id} - actualizar categoría OK")
    void actualizarCategoria_OK() throws Exception {
        Categoria actualizada = new Categoria();
        actualizada.setId(1);
        actualizada.setNombre("Ciencia");

        when(categoriaService.actualizarCategoria(eq(1L), any(Categoria.class))).thenReturn(actualizada);

        mockMvc.perform(put("/categorias/1")
                        .with(jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ciencia"));
    }

    @Test
    @DisplayName("PUT /categorias/{id} - categoría no encontrada")
    void actualizarCategoria_NoEncontrada() throws Exception {
        when(categoriaService.actualizarCategoria(eq(99L), any(Categoria.class)))
                .thenThrow(new RuntimeException("Categoría no encontrada"));

        mockMvc.perform(put("/categorias/99")
                        .with(jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("DELETE /categorias/{id} - eliminar categoría OK")
    void eliminarCategoria_OK() throws Exception {
        mockMvc.perform(delete("/categorias/1").with(jwtUser))
                .andExpect(status().isNoContent());
    }
}
