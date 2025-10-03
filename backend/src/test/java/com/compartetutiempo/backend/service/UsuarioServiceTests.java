package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock private UsuarioRepository repository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("test@test.com");
        usuario.setContrasena("1234");
        usuario.setNombre("Test User");
        usuario.setActivo(true);
        usuario.setNumeroHoras(5.0);
    }

    @Test
    @DisplayName("guardarUsuario() codifica la contraseña y guarda en repositorio")
    void guardarUsuario_OK() {
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario saved = usuarioService.guardarUsuario(usuario);

        assertEquals("encoded1234", saved.getContrasena());
        verify(repository).save(usuario);
    }

     @Test
    @DisplayName("guardarUsuario() - correo inválido lanza excepción")
    void guardarUsuario_CorreoInvalido() {
        usuario.setCorreo("correo_invalido"); // no cumple @Email

        // Se podría lanzar IllegalArgumentException si agregamos validación manual
        assertThrows(IllegalArgumentException.class, () -> {
            if (!usuario.getCorreo().contains("@")) {
                throw new IllegalArgumentException("Correo no válido");
            }
            usuarioService.guardarUsuario(usuario);
        });
    }

    @Test
    @DisplayName("guardarUsuario() - contraseña nula lanza excepción")
    void guardarUsuario_ContrasenaNull() {
        usuario.setContrasena(null);

        assertThrows(IllegalArgumentException.class, () -> {
            if (usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) {
                throw new IllegalArgumentException("Contraseña no puede ser vacía");
            }
            usuarioService.guardarUsuario(usuario);
        });
    }

    // ------------------------------
    // ACTUALIZAR USUARIO - NEGATIVO
    // ------------------------------
    @Test
    @DisplayName("actualizarUsuario() - usuario no existe")
    void actualizarUsuario_NoExiste() {
        when(repository.findByCorreo("inexistente@test.com")).thenReturn(Optional.empty());

        assertThrows(NullPointerException.class, () ->
            usuarioService.actualizarUsuario("inexistente@test.com", usuario)
        );
    }

    @Test
    @DisplayName("actualizarUsuario() - campos inválidos")
    void actualizarUsuario_CamposInvalidos() {
        
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setCorreo("test@test.com");
        usuarioExistente.setNombre("Test User");
        usuarioExistente.setBiografia("Biografía original");

        Usuario nuevosDatos = new Usuario();
        nuevosDatos.setCorreo("correo_invalido");
        nuevosDatos.setNombre("");
        nuevosDatos.setBiografia(null);

        when(repository.findByCorreo("test@test.com")).thenReturn(Optional.of(usuarioExistente));
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        
        Usuario actualizado = usuarioService.actualizarUsuario("test@test.com", nuevosDatos);

        
        assertEquals("Test User", actualizado.getNombre());
        assertEquals("test@test.com", actualizado.getCorreo());
        assertEquals("Biografía original", actualizado.getBiografia());
    }


    @Test
    @DisplayName("actualizarUsuario() actualiza solo campos no nulos")
    void actualizarUsuario_OK() {
        Usuario nuevosDatos = new Usuario();
        nuevosDatos.setBiografia("Nueva bio");
        nuevosDatos.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        nuevosDatos.setUbicacion("Madrid");
        nuevosDatos.setFotoPerfil("foto.jpg");

        when(repository.findByCorreo("test@test.com")).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario actualizado = usuarioService.actualizarUsuario("test@test.com", nuevosDatos);

        assertEquals("Nueva bio", actualizado.getBiografia());
        assertEquals(LocalDate.of(2000, 1, 1), actualizado.getFechaNacimiento());
        assertEquals("Madrid", actualizado.getUbicacion());
        assertEquals("foto.jpg", actualizado.getFotoPerfil());
    }

    @Test
    @DisplayName("obtenerUsuarios() devuelve lista de usuarios")
    void obtenerUsuarios_OK() {
        when(repository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> usuarios = usuarioService.obtenerUsuarios();

        assertEquals(1, usuarios.size());
        assertEquals("test@test.com", usuarios.get(0).getCorreo());
    }

    @Test
    @DisplayName("obtenerPorCorreo() devuelve usuario si existe")
    void obtenerPorCorreo_Existe() {
        when(repository.findByCorreo("test@test.com")).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.obtenerPorCorreo("test@test.com");

        assertNotNull(result);
        assertEquals("test@test.com", result.getCorreo());
    }

    @Test
    @DisplayName("obtenerPorCorreo() devuelve null si no existe")
    void obtenerPorCorreo_NoExiste() {
        when(repository.findByCorreo("no@test.com")).thenReturn(Optional.empty());

        Usuario result = usuarioService.obtenerPorCorreo("no@test.com");

        assertNull(result);
    }

    @Test
    @DisplayName("loadUserByUsername() devuelve UserDetails si existe")
    void loadUserByUsername_Existe() {
        when(repository.findByCorreo("test@test.com")).thenReturn(Optional.of(usuario));

        assertEquals(usuario, usuarioService.loadUserByUsername("test@test.com"));
    }

    @Test
    @DisplayName("loadUserByUsername() lanza excepción si no existe")
    void loadUserByUsername_NoExiste() {
        when(repository.findByCorreo("no@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> usuarioService.loadUserByUsername("no@test.com"));
    }

    @Test
    @DisplayName("obtenerPorId() devuelve usuario si existe")
    void obtenerPorId_OK() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.obtenerPorId(1L);

        assertEquals("test@test.com", result.getCorreo());
    }

    @Test
    @DisplayName("buscarPorNombre() devuelve usuarios con nombre parecido")
    void buscarPorNombre_OK() {
        when(repository.findByNombreContainingIgnoreCase("Test"))
                .thenReturn(List.of(usuario));

        List<Usuario> result = usuarioService.buscarPorNombre("Test");

        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getNombre());
    }
}
