package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.dto.NotificacionDTO;
import com.compartetutiempo.backend.model.Notificacion;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.TipoNotificacion;
import com.compartetutiempo.backend.repository.NotificacionRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificacionServiceTest {

    private NotificacionRepository notificacionRepo;
    private UsuarioService usuarioService;
    private SimpMessagingTemplate messagingTemplate;
    private UsuarioRepository usuarioRepo;

    private NotificacionService notificacionService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        notificacionRepo = Mockito.mock(NotificacionRepository.class);
        usuarioService = Mockito.mock(UsuarioService.class);
        messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        usuarioRepo = Mockito.mock(UsuarioRepository.class);

        notificacionService = new NotificacionService(notificacionRepo, messagingTemplate, usuarioService, usuarioRepo);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("usuario@test.com");
    }

    @Test
    @DisplayName("Crear y enviar notificación")
    void crearYEnviar_OK() {
        when(notificacionRepo.save(any(Notificacion.class))).thenAnswer(i -> i.getArguments()[0]);

        NotificacionDTO dto = notificacionService.crearYEnviar(usuario, TipoNotificacion.RESEÑA, "Contenido", 123);

        assertNotNull(dto);
        assertEquals("Contenido", dto.getContenido());
        verify(messagingTemplate, times(1))
                .convertAndSendToUser(eq("usuario@test.com"), eq("/queue/notifications"), any(NotificacionDTO.class));
        verify(notificacionRepo, times(1)).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Obtener notificaciones de un usuario")
    void getByUsuario_OK() {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioDestino(usuario);
        when(usuarioService.obtenerPorCorreo("usuario@test.com")).thenReturn(usuario);
        when(notificacionRepo.findByUsuarioDestinoOrderByTimestampDesc(usuario)).thenReturn(List.of(notificacion));

        List<Notificacion> notifs = notificacionService.getByUsuario("usuario@test.com");

        assertEquals(1, notifs.size());
        assertEquals(usuario, notifs.get(0).getUsuarioDestino());
    }

    @Test
    @DisplayName("Marcar notificación como leída")
    void marcarComoLeida_OK() {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(10);
        notificacion.setUsuarioDestino(usuario);

        when(usuarioRepo.findByCorreo("usuario@test.com")).thenReturn(Optional.of(usuario));
        when(notificacionRepo.findByIdAndUsuarioDestinoId(10, 1L)).thenReturn(Optional.of(notificacion));

        notificacionService.marcarComoLeida(10, "usuario@test.com");

        verify(notificacionRepo, times(1)).delete(notificacion);
    }

    @Test
    @DisplayName("Marcar todas como leídas")
    void marcarTodasComoLeidas_OK() {
        Notificacion notificacion1 = new Notificacion();
        Notificacion notificacion2 = new Notificacion();
        List<Notificacion> lista = List.of(notificacion1, notificacion2);

        when(usuarioService.obtenerPorCorreo("usuario@test.com")).thenReturn(usuario);
        when(notificacionRepo.findByUsuarioDestinoOrderByTimestampDesc(usuario)).thenReturn(lista);

        notificacionService.marcarTodasComoLeidas("usuario@test.com");

        verify(notificacionRepo, times(1)).deleteAll(lista);
    }
}
