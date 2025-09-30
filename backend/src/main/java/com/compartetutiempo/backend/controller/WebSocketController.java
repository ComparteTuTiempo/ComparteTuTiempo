package com.compartetutiempo.backend.controller;


import java.time.Instant;

import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.compartetutiempo.backend.config.JwtService;
import com.compartetutiempo.backend.dto.MensajeDTO;
import com.compartetutiempo.backend.dto.UsuarioDTO;
import com.compartetutiempo.backend.model.Conversacion;
import com.compartetutiempo.backend.model.Mensaje;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.TipoNotificacion;
import com.compartetutiempo.backend.service.ConversacionService;
import com.compartetutiempo.backend.service.MensajeService;
import com.compartetutiempo.backend.service.NotificacionService;
import com.compartetutiempo.backend.service.UsuarioService;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MensajeService mensajeService;
    private final ConversacionService conversacionService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final NotificacionService notificacionService;

    public WebSocketController(SimpMessagingTemplate messagingTemplate,
    MensajeService mensajeService,
    ConversacionService conversacionService,
    UsuarioService usuarioService,
    JwtService jwtService,
    NotificacionService notificacionService) {
        this.messagingTemplate = messagingTemplate;
        this.mensajeService = mensajeService;
        this.conversacionService = conversacionService;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.notificacionService = notificacionService;
    }

     @MessageMapping("/chat/{conversacionId}")
    public void sendMessage(@DestinationVariable Long conversacionId,
                            @Payload MensajeDTO mensajeDto,
                            SimpMessageHeaderAccessor headerAccessor) {

        // Obtener token guardado en sessionAttributes por el ChannelInterceptor
        String token = (String) headerAccessor.getSessionAttributes().get("token");
        if (token == null || !jwtService.validateToken(token)) {
            System.out.println("❌ Token inválido o ausente");
            return;
        }

        // Extraer correo/username directamente del token
        String correoRemitente = jwtService.getUsernameFromToken(token);
        Usuario remitente = usuarioService.obtenerPorCorreo(correoRemitente);

        // Obtener conversación
        Conversacion conversacion = conversacionService.getById(conversacionId);
        List<Usuario> participantes = conversacion.getParticipantes();

        // Crear y guardar mensaje
        Mensaje mensaje = new Mensaje();
        mensaje.setConversacion(conversacion);
        mensaje.setRemitente(remitente);
        mensaje.setContenido(mensajeDto.getContenido());
        mensaje.setTimestamp(Instant.now());
        mensajeService.guardarMensaje(mensaje);

        UsuarioDTO remitenteDTO = new UsuarioDTO(
            remitente.getId(),
            remitente.getNombre(),
            remitente.getCorreo(),
            remitente.getFotoPerfil(),
            null,
            remitente.isVerificado(),
            remitente.isActivo(),
            remitente.getBiografia(),
            remitente.getFechaNacimiento()
        );

        MensajeDTO response = new MensajeDTO(
            mensaje.getId(),
            mensaje.getContenido(),
            mensaje.getTimestamp(),
            remitenteDTO
        );

        String mensajeNotificacion = "El usuario " + remitente.getNombre() + " ha enviado un mensaje a la conversación " + conversacion.getTitulo();
        for(Usuario participante : participantes){
            if(!participante.equals(remitente)){
                notificacionService.crearYEnviar(participante, TipoNotificacion.MENSAJE, mensajeNotificacion , null);
            }
        }
            

        // Enviar mensaje a todos los suscriptores del topic
        messagingTemplate.convertAndSend("/topic/messages", response);

        System.out.println(" Mensaje enviado por: " + correoRemitente);
    }

    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload String message) {
        messagingTemplate.convertAndSendToUser("userId", "/queue/private", message);
    }
}