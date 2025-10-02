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

        String correoAutor = jwt.getSubject();

        // delega en el service para que setee intercambio y autor
        ResenaIntercambio guardada = resenaService.crear(intercambioId, correoAutor, resena);

        // ahora sí, el intercambio ya no es null
        Usuario destinatario = guardada.getIntercambio().getUser();

        String mensaje = "El usuario " + guardada.getAutor().getNombre()
                + " ha escrito una reseña sobre el intercambio "
                + guardada.getIntercambio().getNombre();

        notificacionService.crearYEnviar(destinatario, TipoNotificacion.INTERCAMBIO, mensaje, null);

        return ResponseEntity.ok(guardada);
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
