package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.model.Resena;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.ResenaRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ResenaService resenaService;

    private Usuario autor;
    private Usuario destinatario;

    @BeforeEach
    void setUp() {
        autor = new Usuario();
        autor.setCorreo("autor@test.com");
        autor.setNombre("Autor");

        destinatario = new Usuario();
        destinatario.setCorreo("dest@test.com");
        destinatario.setNombre("Destinatario");
    }

    @Test
    @DisplayName("crearResena() - éxito")
    void crearResena_OK() {
        when(usuarioRepository.findByCorreo("autor@test.com")).thenReturn(Optional.of(autor));
        when(usuarioRepository.findByCorreo("dest@test.com")).thenReturn(Optional.of(destinatario));
        when(resenaRepository.existsByAutorAndDestinatario(autor, destinatario)).thenReturn(false);
        when(resenaRepository.save(any(Resena.class))).thenAnswer(i -> i.getArgument(0));

        Resena resena = resenaService.crearResena("autor@test.com", "dest@test.com", 5, "Excelente");

        assertNotNull(resena);
        assertEquals(5, resena.getPuntuacion());
        assertEquals("Excelente", resena.getComentario());
        assertEquals(autor, resena.getAutor());
        assertEquals(destinatario, resena.getDestinatario());
    }

    @Test
    @DisplayName("crearResena() - error si el autor es el mismo que destinatario")
    void crearResena_AutorMismoQueDestinatario() {
        when(usuarioRepository.findByCorreo("autor@test.com")).thenReturn(Optional.of(autor));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> resenaService.crearResena("autor@test.com", "autor@test.com", 4, "Auto"));

        assertEquals("No puedes dejarte reseñas a ti mismo", ex.getMessage());
    }

    @Test
    @DisplayName("crearResena() - error si ya existe reseña")
    void crearResena_ResenaDuplicada() {
        when(usuarioRepository.findByCorreo("autor@test.com")).thenReturn(Optional.of(autor));
        when(usuarioRepository.findByCorreo("dest@test.com")).thenReturn(Optional.of(destinatario));
        when(resenaRepository.existsByAutorAndDestinatario(autor, destinatario)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> resenaService.crearResena("autor@test.com", "dest@test.com", 5, "Duplicada"));

        assertEquals("Ya has dejado una reseña a este usuario", ex.getMessage());
    }

    @Test
    @DisplayName("obtenerResenas() - éxito")
    void obtenerResenas_OK() {
        when(usuarioRepository.findByCorreo("dest@test.com")).thenReturn(Optional.of(destinatario));

        Resena r1 = new Resena(); r1.setPuntuacion(5); r1.setComentario("Excelente"); r1.setDestinatario(destinatario);
        Resena r2 = new Resena(); r2.setPuntuacion(3); r2.setComentario("Bueno"); r2.setDestinatario(destinatario);

        when(resenaRepository.findByDestinatario(destinatario)).thenReturn(Arrays.asList(r1, r2));

        List<Resena> resenas = resenaService.obtenerResenas("dest@test.com");

        assertEquals(2, resenas.size());
        assertTrue(resenas.contains(r1));
        assertTrue(resenas.contains(r2));
    }

    @Test
    @DisplayName("calcularPromedio() - retorna promedio correcto")
    void calcularPromedio_OK() {
        when(usuarioRepository.findByCorreo("dest@test.com")).thenReturn(Optional.of(destinatario));

        Resena r1 = new Resena(); r1.setPuntuacion(5); r1.setDestinatario(destinatario);
        Resena r2 = new Resena(); r2.setPuntuacion(3); r2.setDestinatario(destinatario);

        when(resenaRepository.findByDestinatario(destinatario)).thenReturn(Arrays.asList(r1, r2));

        double promedio = resenaService.calcularPromedio("dest@test.com");
        assertEquals(4.0, promedio);
    }

    @Test
    @DisplayName("calcularPromedio() - sin reseñas retorna 0")
    void calcularPromedio_SinResenas() {
        when(usuarioRepository.findByCorreo("dest@test.com")).thenReturn(Optional.of(destinatario));
        when(resenaRepository.findByDestinatario(destinatario)).thenReturn(Collections.emptyList());

        double promedio = resenaService.calcularPromedio("dest@test.com");
        assertEquals(0.0, promedio);
    }
}
