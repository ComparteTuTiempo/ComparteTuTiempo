package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.model.Resena;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.TipoNotificacion;
import com.compartetutiempo.backend.service.NotificacionService;
import com.compartetutiempo.backend.service.ResenaService;
import com.compartetutiempo.backend.service.UsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@CrossOrigin(origins = "http://localhost:3000")
public class ResenaController {

    private final ResenaService resenaService;
    private final NotificacionService notificacionService;
    private final UsuarioService usuarioService;

    public ResenaController(ResenaService resenaService,NotificacionService notificacionService
    ,UsuarioService usuarioService) {
        this.resenaService = resenaService;
        this.notificacionService = notificacionService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/{autorCorreo}/{destinatarioCorreo}")
    public ResponseEntity<?> crearResena(
            @PathVariable String autorCorreo,
            @PathVariable String destinatarioCorreo,
            @RequestParam int puntuacion,
            @RequestParam String comentario) {
        try {
            Resena resena = resenaService.crearResena(autorCorreo, destinatarioCorreo, puntuacion, comentario);

            Usuario destinatario = usuarioService.obtenerPorCorreo(destinatarioCorreo);
            Usuario autor = usuarioService.obtenerPorCorreo(autorCorreo);
            String mensaje = "El usuario " + autor.getNombre() + "Ha escrito una reseña sobre ti";

            notificacionService.crearYEnviar(destinatario, TipoNotificacion.RESEÑA, mensaje, null);
            return ResponseEntity.ok(resena);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{correoDestinatario}")
    public List<Resena> obtenerResenas(@PathVariable String correoDestinatario) {
        return resenaService.obtenerResenas(correoDestinatario);
    }

    @GetMapping("/{correoDestinatario}/promedio")
    public double promedio(@PathVariable String correoDestinatario) {
        return resenaService.calcularPromedio(correoDestinatario);
    }
}
