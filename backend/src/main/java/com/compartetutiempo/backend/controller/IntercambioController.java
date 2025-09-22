package com.compartetutiempo.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.service.IntercambioService;
import com.compartetutiempo.backend.service.UsuarioService;

@RestController
@RequestMapping("/intercambios")
public class IntercambioController {

    private IntercambioService intercambioService;

    private final UsuarioService usuarioService;

    public IntercambioController(IntercambioService intercambioService, UsuarioService usuarioService) {
        this.intercambioService = intercambioService;
        this.usuarioService = usuarioService;

    }

    @PostMapping("/{correo}")
    public ResponseEntity<Intercambio> crear(
            @PathVariable String correo,
            @RequestBody Intercambio intercambio) {
        Intercambio creado = intercambioService.crear(correo, intercambio);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<Intercambio>> obtenerTodos() {
        List<Intercambio> intercambios = intercambioService.obtenerTodos();
        return ResponseEntity.ok(intercambios);
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

    @GetMapping("/usuario")
    public ResponseEntity<List<Intercambio>> obtenerPorUsuario(@AuthenticationPrincipal Jwt jwt) {
        // Sacamos el correo del token
        String correo = jwt.getSubject();
        Usuario user = usuarioService.obtenerPorCorreo(correo);

        List<Intercambio> intercambios = intercambioService.obtenerPorUsuario(user);
        return ResponseEntity.ok(intercambios);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<Intercambio>> obtenerHistorial(@AuthenticationPrincipal Jwt jwt) {
        String correo = jwt.getSubject();
        Usuario user = usuarioService.obtenerPorCorreo(correo);
        return ResponseEntity.ok(intercambioService.obtenerHistorial(user));
    }

}
