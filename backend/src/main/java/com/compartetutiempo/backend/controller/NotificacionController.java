package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.dto.NotificacionDTO;
import com.compartetutiempo.backend.model.Notificacion;
import com.compartetutiempo.backend.service.NotificacionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }
    
    @GetMapping
    public List<NotificacionDTO> getNotificaciones(@AuthenticationPrincipal Jwt jwt) {
        String correoUsuario = jwt.getSubject();
        List<Notificacion> notifs = notificacionService.getByUsuario(correoUsuario);
        return notifs.stream().map(NotificacionDTO::fromEntity).collect(Collectors.toList());
    }
    
    @PutMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Integer id, @AuthenticationPrincipal Jwt jwt) {
        String correoUsuario = jwt.getSubject();
        try {
            notificacionService.marcarComoLeida(id, correoUsuario);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodasComoLeidas(@AuthenticationPrincipal Jwt jwt) {
        String correoUsuario = jwt.getSubject();
        try {
            notificacionService.marcarTodasComoLeidas(correoUsuario);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).build();
        }
    }
}

