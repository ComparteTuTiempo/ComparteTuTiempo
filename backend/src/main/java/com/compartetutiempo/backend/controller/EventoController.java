package com.compartetutiempo.backend.controller;

import org.springframework.http.ResponseEntity;
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


    @PostMapping("/{eventoId}/participar/{usuarioId}")
    public ResponseEntity<Evento> participarEnEvento(
            @PathVariable Long eventoId,
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(eventoService.participarEnEvento(eventoId, usuarioId));
    }

    @PostMapping("/{eventoId}/finalizar")
    public ResponseEntity<Evento> finalizarEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(eventoService.finalizarEvento(eventoId));
    }
}

