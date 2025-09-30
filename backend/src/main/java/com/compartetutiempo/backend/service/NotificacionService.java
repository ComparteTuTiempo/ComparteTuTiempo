package com.compartetutiempo.backend.service;

import java.time.Instant;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compartetutiempo.backend.dto.NotificacionDTO;
import com.compartetutiempo.backend.model.Notificacion;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.TipoNotificacion;
import com.compartetutiempo.backend.repository.NotificacionRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

@Service
public class NotificacionService {
    private final NotificacionRepository notificacionRepository;
    private final UsuarioService usuarioService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UsuarioRepository usuarioRepository;

    public NotificacionService(NotificacionRepository repo, SimpMessagingTemplate template
    ,UsuarioService usuarioService,UsuarioRepository usuarioRepository) {
        this.notificacionRepository = repo;
        this.messagingTemplate = template;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public NotificacionDTO crearYEnviar(Usuario destino, TipoNotificacion tipo, String contenido, Integer referenciaId) {
        Notificacion notif = new Notificacion();
        notif.setUsuarioDestino(destino);
        notif.setTipo(tipo);
        notif.setContenido(contenido);
        notif.setReferenciaId(referenciaId);
        notif.setTimestamp(Instant.now());

        notificacionRepository.save(notif);

        NotificacionDTO dto = NotificacionDTO.fromEntity(notif);

        messagingTemplate.convertAndSendToUser(
            destino.getCorreo(),
            "/queue/notifications",
            dto
        );

        return dto;
    }

    public List<Notificacion> getByUsuario(String correoUsuario) {
        Usuario usuario = usuarioService.obtenerPorCorreo(correoUsuario);
        return notificacionRepository.findByUsuarioDestinoOrderByTimestampDesc(usuario);
    }

    public void marcarComoLeida(Integer id, String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Notificacion notificacion = notificacionRepository.findByIdAndUsuarioDestinoId(id, usuario.getId())
            .orElseThrow(() -> new RuntimeException("Notificación no encontrada o no pertenece al usuario"));

        notificacionRepository.delete(notificacion);
    }


@Transactional
    public void marcarTodasComoLeidas(String correoUsuario) {
        Usuario usuario = usuarioService.obtenerPorCorreo(correoUsuario);
        List<Notificacion> notifs = notificacionRepository.findByUsuarioDestinoOrderByTimestampDesc(usuario);

        notificacionRepository.deleteAll(notifs);
    }

}

