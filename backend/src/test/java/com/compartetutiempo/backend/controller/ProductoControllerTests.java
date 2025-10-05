package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.config.SecurityTestConfig;
import com.compartetutiempo.backend.dto.ProductoDTO;
import com.compartetutiempo.backend.model.Producto;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.service.ProductoService;
import com.compartetutiempo.backend.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductoController.class)
@Import({SecurityTestConfig.class, ProductoControllerTest.MockConfig.class})
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private ProductoService productoService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private ObjectMapper objectMapper;

    private final String correoUsuario = "user@mail.com";

    @BeforeEach
    void setUp() {}

    @TestConfiguration
    static class MockConfig {
        @Bean ProductoService productoService() {
            return Mockito.mock(ProductoService.class);
        }
        @Bean UsuarioService usuarioService() {
            return Mockito.mock(UsuarioService.class);
        }
    }

    // JWT para simular AuthenticationPrincipal
    private final org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser =
            jwt().jwt(jwt -> jwt.subject("user@mail.com"));

    @Test
    @DisplayName("POST /productos - crear producto OK")
    void crear_OK() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setCorreo(correoUsuario);

        Producto producto = new Producto();
        producto.setNombre("Producto Test");
        producto.setNumeroHoras(5);

        when(usuarioService.obtenerPorCorreo(correoUsuario)).thenReturn(usuario);
        when(productoService.crear(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/productos")
                .with(jwtUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Producto Test"));
    }

    @Test
    @DisplayName("GET /productos - obtener todos")
    void obtenerTodos_OK() throws Exception {
        when(productoService.obtenerTodos()).thenReturn(List.of(new Producto() {{
            setNombre("Producto1");
        }}));

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Producto1"));
    }

    @Test
    @DisplayName("GET /productos/{id} - obtener por id")
    void obtenerPorId_OK() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("ProductoDTO");
        when(productoService.obtenerPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("ProductoDTO"));
    }

    @Test
    @DisplayName("PUT /productos/{id} - actualizar producto OK")
    void actualizarProducto_OK() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setCorreo(correoUsuario);
        usuario.setRoles(Set.of());

        Producto productoModificado = new Producto();
        productoModificado.setNombre("Actualizado");
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Actualizado");

        when(usuarioService.obtenerPorCorreo(correoUsuario)).thenReturn(usuario);
        when(productoService.actualizarProducto(eq(1L), any(Producto.class), eq(usuario))).thenReturn(dto);

        mockMvc.perform(put("/productos/1")
                .with(jwtUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoModificado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Actualizado"));
    }

    @Test
    @DisplayName("DELETE /productos/{id} - eliminar producto OK")
    void eliminarProducto_OK() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setCorreo(correoUsuario);

        when(usuarioService.obtenerPorCorreo(correoUsuario)).thenReturn(usuario);
        doNothing().when(productoService).eliminarProducto(eq(1L), eq(usuario));

        mockMvc.perform(delete("/productos/1")
                .with(jwtUser))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /productos/usuario - obtener productos de usuario")
    void obtenerPorUsuario_OK() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setCorreo(correoUsuario);

        when(usuarioService.obtenerPorCorreo(correoUsuario)).thenReturn(usuario);
        when(productoService.obtenerPorUsuario(usuario)).thenReturn(List.of(new Producto() {{
            setNombre("ProductoUsuario");
        }}));

        mockMvc.perform(get("/productos/usuario")
                .with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("ProductoUsuario"));
    }

    @Test
    @DisplayName("GET /productos/historial - obtener historial")
    void historial_OK() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setCorreo(correoUsuario);

        when(usuarioService.obtenerPorCorreo(correoUsuario)).thenReturn(usuario);
        when(productoService.obtenerHistorial(usuario)).thenReturn(List.of(new Producto() {{
            setNombre("ProductoHistorial");
        }}));

        mockMvc.perform(get("/productos/historial")
                .with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("ProductoHistorial"));
    }
}


