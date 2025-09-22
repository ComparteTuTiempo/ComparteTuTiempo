package com.compartetutiempo.backend.controller;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.compartetutiempo.backend.config.JwtService;
import com.compartetutiempo.backend.dto.MensajeDTO;
import com.compartetutiempo.backend.dto.UsuarioDTO;
import com.compartetutiempo.backend.model.Conversacion;
import com.compartetutiempo.backend.model.Mensaje;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.service.ConversacionService;
import com.compartetutiempo.backend.service.MensajeService;
import com.compartetutiempo.backend.service.UsuarioService;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MensajeService mensajeService;
    private final ConversacionService conversacionService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public WebSocketController(SimpMessagingTemplate messagingTemplate,
    MensajeService mensajeService,
    ConversacionService conversacionService,
    UsuarioService usuarioService,
    JwtService jwtService) {
        this.messagingTemplate = messagingTemplate;
        this.mensajeService = mensajeService;
        this.conversacionService = conversacionService;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
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
            remitente.isVerificado()
        );

        MensajeDTO response = new MensajeDTO(
            mensaje.getId(),
            mensaje.getContenido(),
            mensaje.getTimestamp(),
            remitenteDTO
        );

        // Enviar mensaje a todos los suscriptores del topic
        messagingTemplate.convertAndSend("/topic/messages", response);

        System.out.println(" Mensaje enviado por: " + correoRemitente);
    }

    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload String message) {
        messagingTemplate.convertAndSendToUser("userId", "/queue/private", message);
    }
}