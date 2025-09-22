package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.dto.LoginRequest;
import com.compartetutiempo.backend.dto.UsuarioDTO;
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
    public UsuarioDTO obtenerUsuario(@PathVariable String correo) {
        Usuario usuario = service.obtenerPorCorreo(correo);
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setCorreo(correo);
        usuarioDTO.setFotoPerfil(usuario.getFotoPerfil());
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setUbicacion(usuario.getUbicacion());
        usuarioDTO.setVerificado(usuario.isVerificado());
        usuarioDTO.setActivo(usuario.isActivo());
        return usuarioDTO;

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

        if (!usuario.isActivo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario baneado");
        }

        if (!usuario.getContrasena().equals(loginRequest.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/buscar")
    public List<UsuarioDTO> buscarUsuarios(@RequestParam String nombre) {
        List<Usuario> usuarios = service.buscarPorNombre(nombre);

        return usuarios.stream().map(u -> {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setCorreo(u.getCorreo());
            dto.setFotoPerfil(u.getFotoPerfil());
            dto.setNombre(u.getNombre());
            dto.setUbicacion(u.getUbicacion());
            dto.setVerificado(u.isVerificado());
            dto.setActivo(u.isActivo());
            return dto;
        }).toList();
    }
}