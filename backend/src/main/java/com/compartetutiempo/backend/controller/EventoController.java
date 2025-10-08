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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.compartetutiempo.backend.dto.EventoRequest;
import com.compartetutiempo.backend.dto.EventoResponse;
import com.compartetutiempo.backend.dto.ParticipacionDTO;
import com.compartetutiempo.backend.model.Evento;
import com.compartetutiempo.backend.model.Participacion;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.TipoNotificacion;
import com.compartetutiempo.backend.service.EventoService;
import com.compartetutiempo.backend.service.NotificacionService;
import com.compartetutiempo.backend.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;
    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearEvento(@RequestBody EventoRequest request) {
        try{
            Evento evento = new Evento();
            evento.setNombre(request.getNombre());
            evento.setDescripcion(request.getDescripcion());
            evento.setFechaEvento(request.getFechaEvento());
            evento.setUbicacion(request.getUbicacion());
            evento.setDuracion(request.getDuracion());
            evento.setCapacidad(request.getCapacidad());

            Evento creado = eventoService.crearEvento(evento, request.getCorreoOrganizador());
            return ResponseEntity.ok(creado);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        
    }

    @GetMapping("/mis-participaciones")
    public ResponseEntity<List<EventoResponse>> listarMisParticipaciones(
            @RequestParam String correo) {
        return ResponseEntity.ok(eventoService.listarEventosDondeParticipo(correo));
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listarEventos() {
        return ResponseEntity.ok(eventoService.listarEventos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEvento(@PathVariable Integer id) {
        try{
            EventoResponse evento = eventoService.obtenerEventoPorId(id);
            return ResponseEntity.ok(evento);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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

    @PostMapping("/{eventoId}/asistencia")
    public ResponseEntity<String> marcarAsistencia(
            @PathVariable Integer eventoId,
            @RequestParam String correoOrganizador,
            @RequestParam String correoParticipante,
            @RequestParam boolean asistio) {
        try {
            eventoService.marcarAsistencia(eventoId, correoOrganizador, correoParticipante, asistio);
            return ResponseEntity.ok("Asistencia marcada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{eventoId}/finalizar")
    public ResponseEntity<?> finalizarEvento(
            @PathVariable Integer eventoId,
            @RequestParam String correoOrganizador) {
        try {
            EventoResponse evento = eventoService.finalizarEvento(eventoId, correoOrganizador);
            List <ParticipacionDTO> participantes = eventoService.obtenerParticipacionesEvento(eventoId);

            String mensaje = "El evento " + evento.getNombre() + " ha finalizado y se ha hecho el recuento de horas";
            for(ParticipacionDTO participante: participantes){
                Usuario usuarioParticipante = usuarioService.obtenerPorCorreo(participante.getCorreo());
                notificacionService.crearYEnviar(usuarioParticipante, TipoNotificacion.EVENTO, mensaje, null);
            }
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{eventoId}/participantes/lista")
    public ResponseEntity<?> listaParticipantes(
        @PathVariable Integer eventoId,
        @RequestParam String correoOrganizador) {
        try {
            EventoResponse evento = eventoService.obtenerEventoPorId(eventoId);
            if (!evento.getOrganizador().getCorreo().equals(correoOrganizador)) {
                return ResponseEntity.status(403).body("No tienes permisos para gestionar este evento");
            }
            return ResponseEntity.ok(eventoService.obtenerParticipacionesEvento(eventoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

