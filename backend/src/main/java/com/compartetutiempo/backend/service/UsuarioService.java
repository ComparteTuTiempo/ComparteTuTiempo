package com.compartetutiempo.backend.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService{

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository,PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario guardarUsuario(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return repository.save(usuario);
    }

    public List<Usuario> obtenerUsuarios() {
        return repository.findAll();
    }

    public Usuario obtenerPorCorreo(String correo) {
        return repository.findByCorreo(correo).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        return repository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("No se pudo encontrar al usuario con correo : " + correo));
    }
}
