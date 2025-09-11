package com.compartetutiempo.backend.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.compartetutiempo.backend.model.Conversacion;
import com.compartetutiempo.backend.model.Mensaje;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.ConversacionRepository;
import com.compartetutiempo.backend.repository.MensajeRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MensajeService {
    private final MensajeRepository MensajeRepository;
    private final ConversacionRepository conversationRepository;
    private final UsuarioRepository usuarioRepository;

    public Mensaje sendMessage(Long conversacionId, String senderCorreo, String content) {
        Conversacion conversation = conversationRepository.findById(conversacionId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));

        Usuario sender = usuarioRepository.findByCorreo(senderCorreo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Mensaje message = new Mensaje();
        message.setConversacion(conversation);
        message.setRemitente(sender);
        message.setContenido(content);
        message.setTimestamp(Instant.now());

        return MensajeRepository.save(message);
    }

    public List<Mensaje> getMessages(Long conversacionId) {
        return MensajeRepository.findByConversacionIdOrderByTimestampAsc(conversacionId);
    }

    public void guardarMensaje(Mensaje mensaje){
        MensajeRepository.save(mensaje);
    }
}

