package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.dto.AcuerdoRequest;
import com.compartetutiempo.backend.dto.IntercambioDTO;
import com.compartetutiempo.backend.dto.IntercambioUsuarioDTO;
import com.compartetutiempo.backend.dto.NotificacionDTO;
import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.Role;
import com.compartetutiempo.backend.service.IntercambioService;
import com.compartetutiempo.backend.service.IntercambioUsuarioService;
import com.compartetutiempo.backend.service.NotificacionService;
import com.compartetutiempo.backend.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.compartetutiempo.backend.config.SecurityTestConfig;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;


@WebMvcTest(controllers = IntercambioController.class)
@Import({SecurityTestConfig.class, IntercambioControllerTest.MockConfig.class})
class IntercambioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private IntercambioService intercambioService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private IntercambioUsuarioService intercambioUsuarioService;
    @Autowired private NotificacionService notificacionService;
    @Autowired private ObjectMapper objectMapper;

    private final JwtRequestPostProcessor jwtUser = jwt().jwt(jwt -> jwt.subject("user@mail.com"));

    private IntercambioDTO intercambioDTO;
    private IntercambioUsuarioDTO intercambioUsuarioDTO;
    private Usuario usuario;
    private NotificacionDTO notificacionDTO;

   @BeforeEach
    void setUp() {
        intercambioDTO = new IntercambioDTO();
        intercambioDTO.setId(1);
        intercambioDTO.setNombre("Test Intercambio");
        
        intercambioUsuarioDTO = new IntercambioUsuarioDTO();
        intercambioUsuarioDTO.setId(1);
        intercambioUsuarioDTO.setCreadorCorreo("user@mail.com");
        intercambioUsuarioDTO.setSolicitanteCorreo("user@mail.com");
        intercambioUsuarioDTO.setIntercambioNombre("Intercambio Test");
        intercambioUsuarioDTO.setCreadorNombre("Creador");

        usuario = new Usuario();
        usuario.setCorreo("user@mail.com");

        notificacionDTO = new NotificacionDTO();
        notificacionDTO.setId(1);
    }

    @TestConfiguration
    static class MockConfig {
        @Bean IntercambioService intercambioService() {
            return Mockito.mock(IntercambioService.class);
        }
        @Bean UsuarioService usuarioService() {
            return Mockito.mock(UsuarioService.class);
        }
        @Bean IntercambioUsuarioService intercambioUsuarioService() {
            return Mockito.mock(IntercambioUsuarioService.class);
        }
        @Bean NotificacionService notificacionService() {
            return Mockito.mock(NotificacionService.class);
        }
        
    }
    @Test
    @DisplayName("GET /intercambios - debe devolver lista OK")
    void obtenerTodos_OK() throws Exception {
        when(intercambioService.obtenerTodos()).thenReturn(List.of(new Intercambio()));

        mockMvc.perform(get("/intercambios"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("POST /intercambios/{correo} - crea un intercambio")
    void crear_OK() throws Exception {
        Intercambio mock = new Intercambio();
        when(intercambioService.crear(eq("test@mail.com"), any(IntercambioDTO.class)))
            .thenReturn(mock);

        mockMvc.perform(post("/intercambios/test@mail.com")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"nombre\":\"Intercambio Prueba\"}"))
               .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /intercambios/{correo} - debe fallar si el servicio lanza excepción")
    void crear_badrequest() throws Exception {
        when(intercambioService.crear(eq("test@mail.com"), any(IntercambioDTO.class)))
            .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Error"));

        mockMvc.perform(post("/intercambios/test@mail.com")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"nombre\":\"Intercambio Prueba\"}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /intercambios/{id} - devuelve DTO OK")
    void obtenerPorId_OK() throws Exception {
        IntercambioDTO dto = new IntercambioDTO();
        dto.setCorreoOfertante("ofertante@mail.com");

        when(intercambioService.obtenerPorId(1)).thenReturn(dto);

        mockMvc.perform(get("/intercambios/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.correoOfertante").value("ofertante@mail.com"));
    }

    @Test
    @DisplayName("GET /intercambios/{id} - debe fallar si no existe")
    void obtenerPorId_notFound() throws Exception {
        when(intercambioService.obtenerPorId(99))
            .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "No encontrado"));

        mockMvc.perform(get("/intercambios/99"))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /intercambios/{id} - actualiza intercambio")
    void actualizar_OK() throws Exception {
        Intercambio mock = new Intercambio();
        when(intercambioService.actualizarIntercambio(eq(1), any(IntercambioDTO.class)))
            .thenReturn(mock);

        mockMvc.perform(put("/intercambios/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"nombre\":\"Nuevo\"}"))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /intercambios/{id} - usuario dueño puede eliminar")
    void eliminar_OK() throws Exception {
        Usuario user = new Usuario();
        user.setCorreo("owner@mail.com");
        user.setRoles(Set.of(Role.USER));

        IntercambioDTO dto = new IntercambioDTO();
        dto.setCorreoOfertante("owner@mail.com");

        when(usuarioService.obtenerPorCorreo("owner@mail.com")).thenReturn(user);
        when(intercambioService.obtenerPorId(1)).thenReturn(dto);
        doNothing().when(intercambioService).eliminarIntercambio(1);

        mockMvc.perform(delete("/intercambios/1")
            .with(jwt().jwt(jwt -> jwt.subject("owner@mail.com"))))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /intercambios/{id} - usuario sin permisos devuelve 403")
    void eliminar_Forbidden() throws Exception {
        Usuario user = new Usuario();
        user.setCorreo("otro@mail.com");
        user.setRoles(Set.of(Role.USER));

        IntercambioDTO dto = new IntercambioDTO();
        dto.setCorreoOfertante("owner@mail.com");

        when(usuarioService.obtenerPorCorreo("otro@mail.com")).thenReturn(user);
        when(intercambioService.obtenerPorId(1)).thenReturn(dto);

        mockMvc.perform(delete("/intercambios/1")
            .with(jwt().jwt(jwt -> jwt.subject("otro@mail.com"))))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /intercambios/{id}/solicitar - OK")
    void solicitar_OK() throws Exception {
        when(intercambioService.solicitarIntercambio(eq(1), eq("user@mail.com")))
            .thenReturn(intercambioDTO);
        when(intercambioUsuarioService.obtenerPorIntercambioYUsuario(eq(1), eq("user@mail.com")))
            .thenReturn(intercambioUsuarioDTO);
        when(usuarioService.obtenerPorCorreo(anyString())).thenReturn(usuario);
        when(notificacionService.crearYEnviar(any(), any(), any(), any()))
            .thenReturn(notificacionDTO);

        mockMvc.perform(post("/intercambios/1/solicitar").with(jwtUser))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /intercambios/{id}/solicitar - NotFound")
    void solicitar_NotFound() throws Exception {
        when(intercambioService.solicitarIntercambio(99, "user@mail.com"))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        mockMvc.perform(post("/intercambios/99/solicitar").with(jwtUser))
            .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("PUT /intercambios/{id}/acuerdo - OK")
    void establecerAcuerdo_OK() throws Exception {
        AcuerdoRequest request = new AcuerdoRequest();
        request.setHorasAsignadas(5.0);
        request.setTerminos("Condiciones");

        when(intercambioUsuarioService.establecerAcuerdo(eq(1), any(AcuerdoRequest.class), eq("user@mail.com")))
            .thenReturn(intercambioUsuarioDTO);
        when(notificacionService.crearYEnviar(any(), any(), any(), any()))
            .thenReturn(notificacionDTO);

        mockMvc.perform(put("/intercambios/1/acuerdo").with(jwtUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"horasAsignadas\":5,\"terminos\":\"Condiciones\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void establecerAcuerdo_Forbidden() throws Exception {
        AcuerdoRequest req = new AcuerdoRequest();
        req.setHorasAsignadas(5.0);
        req.setTerminos("Condiciones");

        when(intercambioUsuarioService.establecerAcuerdo(eq(1), any(AcuerdoRequest.class), eq("otro@mail.com")))
            .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso"));

        mockMvc.perform(put("/intercambios/1/acuerdo")
                .with(jwt().jwt(jwt -> jwt.subject("otro@mail.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /intercambios/{id}/acuerdo - Bad request (not enough hours)")
    void establecerAcuerdo_BadRequest() throws Exception {
        when(intercambioUsuarioService.establecerAcuerdo(eq(1), any(), eq("user@mail.com")))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No horas"));

        mockMvc.perform(put("/intercambios/1/acuerdo").with(jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"horasAsignadas\":10,\"terminos\":\"Condiciones\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void solicitudes_OK() throws Exception {
        when(intercambioUsuarioService.obtenerSolicitudesPendientes(eq("user@mail.com")))
            .thenReturn(List.of(intercambioUsuarioDTO));

        mockMvc.perform(get("/intercambios/solicitudes").with(jwtUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

   @Test
    @DisplayName("GET /intercambios/solicitudes - Usuario no encontrado")
    void solicitudes_NotFound() throws Exception {
        when(intercambioUsuarioService.obtenerSolicitudesPendientes("user@mail.com"))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        mockMvc.perform(get("/intercambios/solicitudes").with(jwtUser))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /intercambios/{id}/finalizar - OK")
    void finalizar_OK() throws Exception {
        when(intercambioUsuarioService.obtenerPorId(eq(1))).thenReturn(intercambioUsuarioDTO);
        when(usuarioService.obtenerPorCorreo(anyString())).thenReturn(usuario);
        when(intercambioUsuarioService.finalizarAcuerdo(eq(1), eq("user@mail.com")))
            .thenReturn(intercambioUsuarioDTO);
        when(notificacionService.crearYEnviar(any(), any(), any(), any()))
            .thenReturn(notificacionDTO);

        mockMvc.perform(put("/intercambios/1/finalizar").with(jwtUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /intercambios/{id}/finalizar - Forbidden")
    void finalizar_Forbidden() throws Exception {
        when(intercambioUsuarioService.obtenerPorId(eq(1))).thenReturn(intercambioUsuarioDTO);
        when(intercambioUsuarioService.finalizarAcuerdo(eq(1), eq("otro@mail.com")))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes permiso"));

        mockMvc.perform(put("/intercambios/1/finalizar")
                .with(jwt().jwt(jwt -> jwt.claim("sub", "otro@mail.com"))))
            .andExpect(status().isBadRequest());
    }

    // ---------- PUT /solicitudes/{id}/aceptar ----------
    @Test
    @DisplayName("PUT /intercambios/solicitudes/{id}/aceptar - OK")
    void aceptar_OK() throws Exception {
        when(intercambioUsuarioService.aceptarSolicitud(eq(1), eq("user@mail.com")))
            .thenReturn(intercambioDTO);

        mockMvc.perform(put("/intercambios/solicitudes/1/aceptar").with(jwtUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /intercambios/solicitudes/{id}/aceptar - Already in consensus")
    void aceptar_AlreadyConsensus() throws Exception {
        when(intercambioUsuarioService.aceptarSolicitud(eq(1), eq("user@mail.com")))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya en consenso"));

        mockMvc.perform(put("/intercambios/solicitudes/1/aceptar").with(jwtUser))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /intercambios/solicitudes/{id}/rechazar - OK")
    void rechazar_OK() throws Exception {
        when(intercambioUsuarioService.obtenerPorId(eq(1))).thenReturn(intercambioUsuarioDTO);
        when(usuarioService.obtenerPorCorreo(anyString())).thenReturn(usuario);
        doNothing().when(intercambioUsuarioService).rechazarSolicitud(eq(1), eq("user@mail.com"));
        when(notificacionService.crearYEnviar(any(), any(), any(), any()))
            .thenReturn(notificacionDTO);

        mockMvc.perform(put("/intercambios/solicitudes/1/rechazar").with(jwtUser))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /intercambios/solicitudes/{id}/rechazar - Forbidden")
    void rechazar_Forbidden() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No permitido"))
            .when(intercambioUsuarioService).rechazarSolicitud(eq(1), eq("user@mail.com"));

        mockMvc.perform(put("/intercambios/solicitudes/1/rechazar").with(jwtUser))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /intercambios/historial - OK")
    void historial_OK() throws Exception {
        when(usuarioService.obtenerPorCorreo(eq("user@mail.com"))).thenReturn(usuario);
        when(intercambioService.obtenerHistorial(usuario)).thenReturn(List.of(new Intercambio() {{
            setId(1);
            setNombre("Historial Intercambio");
        }}));

        mockMvc.perform(get("/intercambios/historial").with(jwtUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombre").value("Historial Intercambio"));
    }

    @Test
    @DisplayName("GET /intercambios/historial - Usuario no encontrado")
    void historial_NotFound() throws Exception {
        when(usuarioService.obtenerPorCorreo("user@mail.com"))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        mockMvc.perform(get("/intercambios/historial").with(jwtUser))
            .andExpect(status().isNotFound());
    }


}
