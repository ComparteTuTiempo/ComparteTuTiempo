package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.model.ResenaIntercambio;
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

    public ResenaIntercambioController(ResenaIntercambioService resenaService) {
        this.resenaService = resenaService;
    }

    @PostMapping("/{intercambioId}")
    public ResponseEntity<ResenaIntercambio> crear(
            @PathVariable Long intercambioId,
            @RequestBody ResenaIntercambio resena,
            @AuthenticationPrincipal Jwt jwt) {

        String correoAutor = jwt.getSubject();
        return ResponseEntity.ok(resenaService.crear(intercambioId, correoAutor, resena));
    }

    @GetMapping("/{intercambioId}")
    public ResponseEntity<List<ResenaIntercambio>> listar(@PathVariable Long intercambioId) {
        return ResponseEntity.ok(resenaService.obtenerPorIntercambio(intercambioId));
    }

    @GetMapping("/{intercambioId}/promedio")
    public ResponseEntity<Double> promedio(@PathVariable Long intercambioId) {
        return ResponseEntity.ok(resenaService.calcularPromedio(intercambioId));
    }
}
