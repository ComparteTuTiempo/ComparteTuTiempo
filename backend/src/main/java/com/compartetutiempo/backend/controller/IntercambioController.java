package com.compartetutiempo.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.service.IntercambioService;

@RestController
@RequestMapping("/intercambios")
public class IntercambioController {

    private IntercambioService intercambioService;

    public IntercambioController(IntercambioService intercambioService){
        this.intercambioService = intercambioService;

    }

    @PostMapping("/{correo}")
    public ResponseEntity<Intercambio> crear(
        @PathVariable String correo,
        @RequestBody Intercambio intercambio
    ) {
        Intercambio creado = intercambioService.crear(correo, intercambio);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/usuario/{correo}")
    public ResponseEntity<List<Intercambio>> obtenerIntercambiosPorUsuario(@PathVariable String correo) {
        List<Intercambio> intercambios = intercambioService.obtenerPorUsuario(correo);
        return ResponseEntity.ok(intercambios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Intercambio> actualizarIntercambio(
        @PathVariable Long id,
        @RequestBody Intercambio intercambioModificado) {

        Intercambio actualizado = intercambioService.actualizarIntercambio(id, intercambioModificado);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Intercambio> obtenerIntercambio(@PathVariable Long id) {
        Intercambio intercambio = intercambioService.obtenerPorId(id);
        return ResponseEntity.ok(intercambio);
    }
    
}
