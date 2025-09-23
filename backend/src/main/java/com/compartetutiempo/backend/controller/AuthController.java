package com.compartetutiempo.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.compartetutiempo.backend.config.JwtService;
import com.compartetutiempo.backend.dto.AuthRequest;
import com.compartetutiempo.backend.dto.AuthResponse;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.service.UsuarioService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

@PostMapping("/login")
public AuthResponse login(@RequestBody AuthRequest request) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getContraseña())
    );

    // Obtener el usuario completo desde la base de datos
    Usuario usuario = usuarioService.obtenerPorCorreo(request.getCorreo());

    // Si los roles no se cargan automáticamente desde la relación, asegúrate de cargarlos
    // usuario.setRoles(usuario.getRolesFromDB()); // solo si necesitas sincronizar desde usuario_roles

    // Generar token con roles correctos
    String token = jwtService.generateToken(usuario);

    return new AuthResponse(token);
}


}

