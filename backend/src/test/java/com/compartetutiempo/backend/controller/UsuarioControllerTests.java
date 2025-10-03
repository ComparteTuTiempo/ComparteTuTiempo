package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.config.SecurityTestConfig;
import com.compartetutiempo.backend.dto.LoginRequest;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.Role;
import com.compartetutiempo.backend.service.UsuarioService;
import com.compartetutiempo.backend.config.JwtService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UsuarioController.class)
@Import({SecurityTestConfig.class, UsuarioControllerTest.MockConfig.class})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;

    // JWT para simular usuario autenticado
    private final org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser =
            jwt().jwt(jwt -> jwt.subject("user@test.com"));

    @TestConfiguration
    static class MockConfig {
        @Bean
        UsuarioService usuarioService() { return Mockito.mock(UsuarioService.class); }
        @Bean
        JwtService jwtService() { return Mockito.mock(JwtService.class); }
    }

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setCorreo("user@test.com");
        usuario.setNombre("Test User");
        usuario.setActivo(true);
        usuario.setVerificado(true);
        usuario.setContrasena("password");
        usuario.setRoles(Set.of(Role.USER));
        usuario.setNumeroHoras(5.0);
    }

    @Test
    @DisplayName("GET /api/usuarios/me - obtener usuario actual OK")
    void getCurrentUser_OK() throws Exception {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuario);

        mockMvc.perform(get("/api/usuarios/me").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("user@test.com"))
                .andExpect(jsonPath("$.nombre").value("Test User"))
                .andExpect(jsonPath("$.numeroHoras").value(5.0));
    }

    @Test
    @DisplayName("POST /api/usuarios/login - login OK")
    void login_OK() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setCorreo("user@test.com");
        loginRequest.setContrasena("password");

        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("user@test.com"));
    }

    @Test
    @DisplayName("POST /api/usuarios/login - usuario no encontrado")
    void login_UsuarioNoEncontrado() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setCorreo("inexistente@test.com");
        loginRequest.setContrasena("password");

        when(usuarioService.obtenerPorCorreo("inexistente@test.com")).thenReturn(null);

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Usuario no encontrado"));
    }

    @Test
    @DisplayName("GET /api/usuarios - listar usuarios")
    void listarUsuarios_OK() throws Exception {
        when(usuarioService.obtenerUsuarios()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/usuarios").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correo").value("user@test.com"));
    }

    @Test
    @DisplayName("GET /api/usuarios/{correo} - obtener usuario por correo")
    void obtenerUsuario_OK() throws Exception {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuario);

        mockMvc.perform(get("/api/usuarios/user@test.com").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("user@test.com"))
                .andExpect(jsonPath("$.nombre").value("Test User"));
    }

    @Test
    @DisplayName("POST /api/usuarios - crear usuario")
    void crearUsuario_OK() throws Exception {
        when(usuarioService.guardarUsuario(any())).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("user@test.com"));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{correo} - actualizar todos los campos")
    void actualizarUsuario_TodosCampos_OK() throws Exception {
        Usuario nuevosDatos = new Usuario();
        nuevosDatos.setBiografia("Nueva biografía");
        nuevosDatos.setFechaNacimiento(LocalDate.of(1995, 5, 20));
        nuevosDatos.setUbicacion("Barcelona");
        nuevosDatos.setFotoPerfil("foto.jpg");

        Usuario actualizado = new Usuario();
        actualizado.setCorreo("user@test.com");
        actualizado.setBiografia("Nueva biografía");
        actualizado.setFechaNacimiento(LocalDate.of(1995, 5, 20));
        actualizado.setUbicacion("Barcelona");
        actualizado.setFotoPerfil("foto.jpg");

        when(usuarioService.actualizarUsuario(eq("user@test.com"), any(Usuario.class)))
                .thenReturn(actualizado);

        mockMvc.perform(put("/api/usuarios/user@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevosDatos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("user@test.com"))
                .andExpect(jsonPath("$.biografia").value("Nueva biografía"))
                .andExpect(jsonPath("$.fechaNacimiento").value("1995-05-20"))
                .andExpect(jsonPath("$.ubicacion").value("Barcelona"))
                .andExpect(jsonPath("$.fotoPerfil").value("foto.jpg"));
    }

    @Test
    @DisplayName("POST /api/usuarios/login/google - login Google")
    void loginGoogle_OK() throws Exception {
        Map<String, String> request = Map.of("correo", "user@test.com", "nombre", "Test User");
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuario);
        when(jwtService.generateToken(usuario)).thenReturn("TOKEN123");

        mockMvc.perform(post("/api/usuarios/login/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("TOKEN123"));
    }

}
