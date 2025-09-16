package com.compartetutiempo.backend.service;

import java.util.ArrayList;
import java.util.List;

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

    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    public void guardarEvento(Evento evento){
        eventoRepository.save(evento);
    }

    public Evento obtenerEventoPorId(Integer id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con id: " + id));
    }

    public List<Usuario> obtenerParticipantesEvento(Integer id){
        Evento evento = obtenerEventoPorId(id);
        return evento.getParticipantes();
    }

    @Transactional
    public Evento participarEnEvento(Integer eventoId, String correo) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Si la lista de participantes es nula, inicializarla
        if (evento.getParticipantes() == null) {
            evento.setParticipantes(new ArrayList<>());
        }else if(evento.getOrganizador().getCorreo().equals(correo)){
            throw new RuntimeException("El anfitrión no puede registrar su participación en su propio evento");
        }

        boolean yaInscrito = evento.getParticipantes().stream()
                .anyMatch(p -> p.getCorreo().equals(usuario.getCorreo()));

        if (yaInscrito) {
            throw new RuntimeException("El usuario ya está inscrito en este evento");
        }

        evento.getParticipantes().add(usuario);
        return eventoRepository.save(evento);
    }


    @Transactional
    public Evento finalizarEvento(Integer eventoId) {
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

