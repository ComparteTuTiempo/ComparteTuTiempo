package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.dto.LoginRequest;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.UsuarioRepository;
import com.compartetutiempo.backend.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000") // React
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return service.obtenerUsuarios();
    }

    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return service.guardarUsuario(usuario);
    }

    @GetMapping("/{correo}")
    public  Usuario obtenerUsuario(@PathVariable String correo) {
        return service.obtenerPorCorreo(correo);
    }

    @PutMapping("/{correo}")
    public ResponseEntity<Usuario> actualizarUsuario(
        @PathVariable String correo,
        @RequestBody Usuario usuarioModificado) {
    
        Usuario actualizado = service.actualizarUsuario(correo, usuarioModificado);
        return ResponseEntity.ok(actualizado);
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    Usuario usuario = service.obtenerPorCorreo(loginRequest.getCorreo());

    if (usuario == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
    }

    if (!usuario.getContrasena().equals(loginRequest.getContrasena())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
    }

    return ResponseEntity.ok(usuario);
}
}