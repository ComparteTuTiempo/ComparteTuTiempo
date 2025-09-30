package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.model.ResenaIntercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.TipoNotificacion;
import com.compartetutiempo.backend.service.NotificacionService;
import com.compartetutiempo.backend.service.ResenaIntercambioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resenas/intercambios")
public class ResenaIntercambioController {

    private final ResenaIntercambioService resenaService;
    private final NotificacionService notificacionService;

    public ResenaIntercambioController(ResenaIntercambioService resenaService,
    NotificacionService notificacionService) {
        this.resenaService = resenaService;
        this.notificacionService = notificacionService;
    }

    @PostMapping("/{intercambioId}")
    public ResponseEntity<ResenaIntercambio> crear(
            @PathVariable Integer intercambioId,
            @RequestBody ResenaIntercambio resena,
            @AuthenticationPrincipal Jwt jwt) {

        Usuario destinatario = resena.getIntercambio().getUser();

        String mensaje = "El usuario " + resena.getAutor() + "ha escrito una reseña sobre el intercambio" 
        + resena.getIntercambio().getNombre();
        notificacionService.crearYEnviar(destinatario, TipoNotificacion.INTERCAMBIO, mensaje, null);

        String correoAutor = jwt.getSubject();
        return ResponseEntity.ok(resenaService.crear(intercambioId, correoAutor, resena));
    }

    @GetMapping("/{intercambioId}")
    public ResponseEntity<List<ResenaIntercambio>> listar(@PathVariable Integer intercambioId) {
        return ResponseEntity.ok(resenaService.obtenerPorIntercambio(intercambioId));
    }

    @GetMapping("/{intercambioId}/promedio")
    public ResponseEntity<Double> promedio(@PathVariable Integer intercambioId) {
        return ResponseEntity.ok(resenaService.calcularPromedio(intercambioId));
    }
}
