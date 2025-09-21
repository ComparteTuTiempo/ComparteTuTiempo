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

    public Usuario guardar(Usuario usuario) {
        return repository.save(usuario);
    }

    public Usuario actualizarUsuario(String correo, Usuario nuevosDatos) {
        Usuario usuario = obtenerPorCorreo(correo);

        usuario.setBiografia(
            nuevosDatos.getBiografia() != null ? nuevosDatos.getBiografia() : usuario.getBiografia()
        );
        usuario.setFechaNacimiento(
            nuevosDatos.getFechaNacimiento() != null ? nuevosDatos.getFechaNacimiento() : usuario.getFechaNacimiento()
        );
        usuario.setUbicacion(
            nuevosDatos.getUbicacion() != null ? nuevosDatos.getUbicacion() : usuario.getUbicacion()
        );

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

    public Usuario obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }
}
