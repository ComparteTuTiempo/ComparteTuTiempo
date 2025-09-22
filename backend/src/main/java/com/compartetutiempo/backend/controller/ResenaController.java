package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.model.Resena;
import com.compartetutiempo.backend.service.ResenaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@CrossOrigin(origins = "http://localhost:3000")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @PostMapping("/{autorCorreo}/{destinatarioCorreo}")
    public ResponseEntity<?> crearResena(
            @PathVariable String autorCorreo,
            @PathVariable String destinatarioCorreo,
            @RequestParam int puntuacion,
            @RequestParam String comentario) {
        try {
            Resena resena = resenaService.crearResena(autorCorreo, destinatarioCorreo, puntuacion, comentario);
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
