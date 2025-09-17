package com.compartetutiempo.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.compartetutiempo.backend.dto.EventoResponse;
import com.compartetutiempo.backend.dto.ParticipacionDTO;
import com.compartetutiempo.backend.model.Evento;
import com.compartetutiempo.backend.model.Participacion;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoEvento;
import com.compartetutiempo.backend.repository.EventoRepository;
import com.compartetutiempo.backend.repository.ParticipacionRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ParticipacionRepository participacionRepository;

    @Transactional
    public Evento crearEvento(Evento evento, String correoOrganizador) {
        Usuario organizador = usuarioRepository.findByCorreo(correoOrganizador)
        .orElseThrow(() -> new RuntimeException("Organizador no encontrado"));
        
        evento.setOrganizador(organizador);
        return eventoRepository.save(evento);
    }

    public List<EventoResponse> listarEventos() {
        return eventoRepository.findAll().stream()
            .map(EventoResponse::mapToDTO)
            .toList();
    }

    public void guardarEvento(Evento evento){
        eventoRepository.save(evento);
    }

    public EventoResponse obtenerEventoPorId(Integer id) {
        Evento evento = eventoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado con id: " + id));
        return EventoResponse.mapToDTO(evento);
    }

    @Transactional
    public Participacion participarEnEvento(Integer eventoId, String correo) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean yaInscrito = participacionRepository.findByEventoId(eventoId).stream()
                .anyMatch(p -> p.getUsuario().getCorreo().equals(usuario.getCorreo()));

        boolean esAnfitrion = evento.getOrganizador().getCorreo().equals(correo);
        if (yaInscrito) {
            throw new RuntimeException("El usuario ya está inscrito en este evento");
        }else if(esAnfitrion){
            throw new RuntimeException("El anfitrión no puede inscribirse en su propio evento");
        }

        Participacion participacion = new Participacion();
        participacion.setEvento(evento);
        participacion.setUsuario(usuario);
        participacion.setAsistio(false);

        return participacionRepository.save(participacion);
    }

    public List<ParticipacionDTO> obtenerParticipacionesEvento(Integer eventoId) {
    List<Participacion> participaciones = participacionRepository.findByEventoId(eventoId);

    return participaciones.stream()
            .map(p -> new ParticipacionDTO(
                    p.getUsuario().getCorreo(),
                    p.getUsuario().getNombre(),
                    p.getUsuario().getFotoPerfil(),
                    p.isAsistio()
            ))
            .toList();
    }


    @Transactional
    public EventoResponse finalizarEvento(Integer eventoId, String correoOrganizador) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        if (!evento.getOrganizador().getCorreo().equals(correoOrganizador)) {
            throw new RuntimeException("Solo el organizador puede finalizar el evento");
        }
        
        else if (evento.getFechaEvento().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("No puedes finalizar un evento que aún no ha ocurrido");
        }
        
        else if (evento.getEstadoEvento() == EstadoEvento.FINALIZADO) {
            throw new RuntimeException("El evento ya fue finalizado");
        }

        // Sumar horas solo a quienes asistieron
        for (Participacion p : participacionRepository.findByEventoId(eventoId)) {
            if (p.isAsistio()) {
                Usuario usuario = p.getUsuario();
                usuario.setNumeroHoras(usuario.getNumeroHoras() + evento.getDuracion());
                usuarioRepository.save(usuario);
            }
        }

        evento.setEstadoEvento(EstadoEvento.FINALIZADO);
        eventoRepository.save(evento);

        EventoResponse eventoDTO = EventoResponse.mapToDTO(evento);
        return eventoDTO;


    }


    @Transactional
    public void marcarAsistencia(Integer eventoId, String correoOrganizador, String correoParticipante, boolean asistio) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        if (!evento.getOrganizador().getCorreo().equals(correoOrganizador)) {
            throw new RuntimeException("Solo el organizador puede marcar asistencia");
        }

        Participacion participacion = participacionRepository.findByEventoIdAndUsuarioCorreo(eventoId, correoParticipante)
                .orElseThrow(() -> new RuntimeException("Participación no encontrada"));

        participacion.setAsistio(asistio);
        participacionRepository.save(participacion);
    }

}

