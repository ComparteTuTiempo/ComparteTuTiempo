package com.compartetutiempo.backend.controller;

import java.util.ArrayList;
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
import com.compartetutiempo.backend.model.Evento;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.UsuarioRepository;
import com.compartetutiempo.backend.service.EventoService;
import com.compartetutiempo.backend.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;
    private final UsuarioService usuarioService;

    @PostMapping("/crear")
    public ResponseEntity<Evento> crearEvento(@RequestBody EventoRequest request) {
        Evento evento = new Evento();
        evento.setNombre(request.getNombre());
        evento.setDescripcion(request.getDescripcion());
        evento.setFechaEvento(request.getFechaEvento());
        evento.setDuracion(request.getDuracion());

        Evento creado = eventoService.crearEvento(evento, request.getCorreoOrganizador());
        return ResponseEntity.ok(creado);
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listarEventos() {
        return ResponseEntity.ok(eventoService.listarEventos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtenerEvento(@PathVariable Integer id) {
        Evento evento = eventoService.obtenerEventoPorId(id);
        return ResponseEntity.ok(evento);
    }

    @GetMapping("/{id}/participantes")
    public ResponseEntity<List<Usuario>> obtenerParticipantesEvento(@PathVariable Integer id) {
        List<Usuario> participantes = eventoService.obtenerParticipantesEvento(id);
        return ResponseEntity.ok(participantes);
    }


    @PostMapping("/{eventoId}/participar/{correo}")
    public ResponseEntity<?> registrarParticipacion(
        @PathVariable Integer eventoId,
        @PathVariable String correo) {
        try {
            Evento evento = eventoService.participarEnEvento(eventoId, correo);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/{eventoId}/finalizar")
    public ResponseEntity<Evento> finalizarEvento(@PathVariable Integer eventoId) {
        return ResponseEntity.ok(eventoService.finalizarEvento(eventoId));
    }
}

