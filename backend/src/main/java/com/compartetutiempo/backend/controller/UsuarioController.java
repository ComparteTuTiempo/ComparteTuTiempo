package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.service.UsuarioService;
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
    public Usuario obtenerUsuario(@PathVariable String correo) {
        return service.obtenerPorCorreo(correo);
    }
}