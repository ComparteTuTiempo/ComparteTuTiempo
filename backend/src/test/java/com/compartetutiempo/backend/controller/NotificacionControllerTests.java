package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.config.SecurityTestConfig;
import com.compartetutiempo.backend.model.Notificacion;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.TipoNotificacion;
import com.compartetutiempo.backend.service.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.RequestPostProcessor;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificacionController.class)
@Import({SecurityTestConfig.class, NotificacionControllerTest.MockConfig.class})
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificacionService notificacionService;

    private final String correoUsuario = "user@test.com";

    // JWT simulado
    private final org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser =
            jwt().jwt(j -> j.subject(correoUsuario));

    @TestConfiguration
    static class MockConfig {
        @Bean
        NotificacionService notificacionService() {
            return Mockito.mock(NotificacionService.class);
        }
    }

    private Notificacion notif1;

    private Usuario mockUsuario;

    @BeforeEach
    void setUp() {
        mockUsuario = new Usuario();
        mockUsuario.setCorreo("user@test.com");

        notif1 = new Notificacion();
        notif1.setId(1);
        notif1.setTipo(TipoNotificacion.RESEÑA);
        notif1.setContenido("Nueva reseña");
        notif1.setTimestamp(Instant.now());
    }

    @Test
    @DisplayName("GET /notificaciones - obtener notificaciones OK")
    void getNotificaciones_OK() throws Exception {
        when(notificacionService.getByUsuario(correoUsuario)).thenReturn(List.of(notif1));

        mockMvc.perform(get("/notificaciones").with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].contenido").value("Nueva reseña"));
    }

    @Test
    @DisplayName("PUT /notificaciones/{id}/leer - marcar como leída OK")
    void marcarComoLeida_OK() throws Exception {
        mockMvc.perform(put("/notificaciones/1/leer").with(jwtUser))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /notificaciones/{id}/leer - notificación no encontrada")
    void marcarComoLeida_NotFound() throws Exception {
        doThrow(new RuntimeException("Notificación no encontrada"))
                .when(notificacionService).marcarComoLeida(99, correoUsuario);

        mockMvc.perform(put("/notificaciones/99/leer").with(jwtUser))
                .andExpect(status().isNotFound());
    }

    @Test
    void marcarTodasComoLeidas_OK() throws Exception {
        // Configuramos el servicio para que no lance excepciones
        doNothing().when(notificacionService).marcarTodasComoLeidas("user@test.com");

        // Creamos un JWT simulado con el subject correcto
        RequestPostProcessor jwtToken = jwt().jwt(builder -> builder.subject("user@test.com"));

        // Llamamos al endpoint y esperamos 200 OK
        mockMvc.perform(put("/notificaciones/leer-todas").with(jwtToken))
                .andExpect(status().isOk());

        // Verificamos que el servicio fue llamado exactamente una vez
        verify(notificacionService, atLeastOnce()).marcarTodasComoLeidas("user@test.com");
    }

    @Test
    @DisplayName("PUT /notificaciones/leer-todas - error interno")
    void marcarTodasComoLeidas_Error() throws Exception {
        doThrow(new RuntimeException("Error interno"))
                .when(notificacionService).marcarTodasComoLeidas(correoUsuario);

        mockMvc.perform(put("/notificaciones/leer-todas").with(jwtUser))
                .andExpect(status().isBadRequest());
    }
}





