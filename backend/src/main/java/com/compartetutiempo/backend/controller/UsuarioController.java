package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.config.JwtService;
import com.compartetutiempo.backend.dto.LoginRequest;
import com.compartetutiempo.backend.dto.UsuarioDTO;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.Role;
import com.compartetutiempo.backend.repository.UsuarioRepository;
import com.compartetutiempo.backend.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000") // React
public class UsuarioController {

    private final UsuarioService service;

    private final JwtService jwtService;

    public UsuarioController(UsuarioService service, JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return service.obtenerUsuarios();
    }

    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        usuario.setMetodoAutenticacion("CORREO");
        ;
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
        usuarioDTO.setBiografia(usuario.getBiografia());
        usuarioDTO.setFechaNacimiento(usuario.getFechaNacimiento());
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

    @PostMapping("/login/google")
    public ResponseEntity<?> loginGoogle(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        String nombre = request.get("nombre");

        Usuario usuario = service.obtenerPorCorreo(correo);
        System.err.println("USUARIOS" + usuario);
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setCorreo(correo);
            usuario.setNombre(nombre);
            usuario.setMetodoAutenticacion("GOOGLE");
            usuario.setActivo(true);
            usuario.setVerificado(true);
            usuario.setContrasena("GOOGLE_PASSWORD");
            usuario.setRoles(Set.of(Role.USER));
            usuario = service.guardarUsuario(usuario);
        }

        if (!usuario.isActivo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario baneado");
        }

        // 👇 Generar token JWT igual que en el login normal
        String token = jwtService.generateToken(usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("roles", usuario.getRoles());
        response.put("correo", usuario.getCorreo());
        response.put("nombre", usuario.getNombre());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/facebook")
    public ResponseEntity<?> loginFacebook(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        String nombre = request.get("nombre");

        Usuario usuario = service.obtenerPorCorreo(correo);
        System.out.println("📩 Datos recibidos de Facebook: " + request);
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setCorreo(correo);
            usuario.setNombre(nombre);
            usuario.setMetodoAutenticacion("FACEBOOK");
            usuario.setActivo(true);
            usuario.setVerificado(true);
            usuario.setContrasena("FACEBOOK_PASSWORD");
            usuario.setRoles(Set.of(Role.USER));
            usuario = service.guardar(usuario);
        }

        if (!usuario.isActivo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario baneado");
        }

        // 👇 Generar token JWT igual que en el login normal
        String token = jwtService.generateToken(usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("roles", usuario.getRoles());
        response.put("correo", usuario.getCorreo());
        response.put("nombre", usuario.getNombre());

        return ResponseEntity.ok(response);
    }

}