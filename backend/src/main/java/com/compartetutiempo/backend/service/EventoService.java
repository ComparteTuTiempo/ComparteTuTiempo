package com.compartetutiempo.backend.service;

import org.springframework.stereotype.Service;

import com.compartetutiempo.backend.model.Evento;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.EventoRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Evento crearEvento(Evento evento, String correoOrganizador) {
        Usuario organizador = usuarioRepository.findByCorreo(correoOrganizador)
        .orElseThrow(() -> new RuntimeException("Organizador no encontrado"));
        evento.setOrganizador(organizador);
        return eventoRepository.save(evento);
    }


    
    @Transactional
    public Evento participarEnEvento(Long eventoId, Long usuarioId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        evento.getParticipantes().add(usuario);
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento finalizarEvento(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        Double horas = evento.getDuracion();
        for (Usuario participante : evento.getParticipantes()) {
            participante.setNumeroHoras(participante.getNumeroHoras() + horas);
            usuarioRepository.save(participante);
        }

        return evento;
    }
}

