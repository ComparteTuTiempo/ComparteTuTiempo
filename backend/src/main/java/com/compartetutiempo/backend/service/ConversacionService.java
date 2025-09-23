package com.compartetutiempo.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.compartetutiempo.backend.model.Conversacion;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.ConversacionRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversacionService {
    private final ConversacionRepository conversationRepository;
    private final UsuarioRepository usuarioRepository;

    public Conversacion createConversation(List<String> correos, String title) {
        List<Usuario> participantes = usuarioRepository.findByCorreoIn(correos);
        Conversacion conversacion = new Conversacion();
        conversacion.setTitulo(title);
        conversacion.setParticipantes(participantes);
        return conversationRepository.save(conversacion);
    }

    public List<Conversacion> getUserConversations(String correo) {
        return conversationRepository.findByUserCorreo(correo);
    }

    public Conversacion getById(Long id) {
        Optional<Conversacion> conversacion = conversationRepository.findById(id);
        return conversacion.isPresent()?conversacion.get():null;
    }
}

