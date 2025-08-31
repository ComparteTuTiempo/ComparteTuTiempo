package com.compartetutiempo.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return repository.save(usuario);
    }

    public List<Usuario> obtenerUsuarios() {
        return repository.findAll();
    }

    public Usuario obtenerPorCorreo(String correo) {
        return repository.findByCorreo(correo).orElse(null);
    }
}
