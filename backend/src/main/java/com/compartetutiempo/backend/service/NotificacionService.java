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

@Service
public class NotificacionService {
    private final NotificacionRepository notificacionRepository;
    private final UsuarioService usuarioService;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificacionService(NotificacionRepository repo, SimpMessagingTemplate template
    ,UsuarioService usuarioService) {
        this.notificacionRepository = repo;
        this.messagingTemplate = template;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public NotificacionDTO crearYEnviar(Usuario destino, TipoNotificacion tipo, String contenido, Integer referenciaId) {
        Notificacion notif = new Notificacion();
        notif.setUsuarioDestino(destino);
        notif.setTipo(tipo);
        notif.setContenido(contenido);
        notif.setReferenciaId(referenciaId);
        notif.setTimestamp(Instant.now());
        notif.setLeida(false);

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
        Notificacion notif = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        if (!notif.getUsuarioDestino().getCorreo().equals(correoUsuario)) {
            throw new RuntimeException("No autorizado");
        }

        notif.setLeida(true);
        notificacionRepository.save(notif);
    }

    public void marcarTodasComoLeidas(String correoUsuario) {
        Usuario usuario = usuarioService.obtenerPorCorreo(correoUsuario);
        List<Notificacion> notifs = notificacionRepository.findByUsuarioDestinoOrderByTimestampDesc(usuario);
        notifs.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(notifs);
    }


}

