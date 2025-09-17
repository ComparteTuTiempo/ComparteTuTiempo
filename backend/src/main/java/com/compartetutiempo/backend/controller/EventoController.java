package com.compartetutiempo.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compartetutiempo.backend.dto.EventoRequest;
import com.compartetutiempo.backend.dto.EventoResponse;
import com.compartetutiempo.backend.dto.ParticipacionDTO;
import com.compartetutiempo.backend.model.Evento;
import com.compartetutiempo.backend.model.Participacion;
import com.compartetutiempo.backend.service.EventoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @PostMapping("/crear")
    public ResponseEntity<Evento> crearEvento(@RequestBody EventoRequest request) {
        Evento evento = new Evento();
        evento.setNombre(request.getNombre());
        evento.setDescripcion(request.getDescripcion());
        evento.setFechaEvento(request.getFechaEvento());
        evento.setUbicacion(request.getUbicacion());
        evento.setDuracion(request.getDuracion());

        Evento creado = eventoService.crearEvento(evento, request.getCorreoOrganizador());
        return ResponseEntity.ok(creado);
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listarEventos() {
        return ResponseEntity.ok(eventoService.listarEventos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> obtenerEvento(@PathVariable Integer id) {
        EventoResponse evento = eventoService.obtenerEventoPorId(id);
        return ResponseEntity.ok(evento);
    }

    @GetMapping("/{id}/participantes")
    public ResponseEntity<List<ParticipacionDTO>> obtenerParticipacionesEvento(@PathVariable Integer id) {
        return ResponseEntity.ok(eventoService.obtenerParticipacionesEvento(id));
    }


    @PostMapping("/{eventoId}/participar/{correo}")
    public ResponseEntity<?> registrarParticipacion(
        @PathVariable Integer eventoId,
        @PathVariable String correo) {
        try {
            Participacion nuevaParticipacion = eventoService.participarEnEvento(eventoId, correo);
            return ResponseEntity.ok(nuevaParticipacion);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }   


    @PostMapping("/{eventoId}/finalizar")
    public ResponseEntity<Evento> finalizarEvento(@PathVariable Integer eventoId) {
        return ResponseEntity.ok(eventoService.finalizarEvento(eventoId));
    }
}

