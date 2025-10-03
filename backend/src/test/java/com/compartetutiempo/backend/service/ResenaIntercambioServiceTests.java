package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.ResenaIntercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.IntercambioRepository;
import com.compartetutiempo.backend.repository.ReseñaIntercambioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ResenaIntercambioServiceTest {

    private ResenaIntercambioService service;
    private ReseñaIntercambioRepository reseñaRepo;
    private IntercambioRepository intercambioRepo;
    private UsuarioRepository usuarioRepo;

    private Usuario autor;
    private Intercambio intercambio;

    @BeforeEach
    void setUp() {
        reseñaRepo = Mockito.mock(ReseñaIntercambioRepository.class);
        intercambioRepo = Mockito.mock(IntercambioRepository.class);
        usuarioRepo = Mockito.mock(UsuarioRepository.class);

        service = new ResenaIntercambioService(reseñaRepo, intercambioRepo, usuarioRepo);

        autor = new Usuario();
        autor.setCorreo("autor@test.com");
        autor.setNombre("Autor");

        intercambio = new Intercambio();
        intercambio.setId(1);
        intercambio.setNombre("Intercambio Test");
        intercambio.setUser(new Usuario());
    }

    @Test
    @DisplayName("crear() - reseña creada correctamente")
    void crear_OK() {
        ResenaIntercambio reseña = new ResenaIntercambio();
        reseña.setPuntuacion(5);
        reseña.setComentario("Excelente");

        when(intercambioRepo.findById(1)).thenReturn(Optional.of(intercambio));
        when(usuarioRepo.findByCorreo("autor@test.com")).thenReturn(Optional.of(autor));
        when(reseñaRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResenaIntercambio resultado = service.crear(1, "autor@test.com", reseña);

        assertNotNull(resultado);
        assertEquals(5, resultado.getPuntuacion());
        assertEquals("Excelente", resultado.getComentario());
        assertEquals(autor, resultado.getAutor());
        assertEquals(intercambio, resultado.getIntercambio());
    }

    @Test
    @DisplayName("crear() - error intercambio no encontrado")
    void crear_ErrorIntercambioNoEncontrado() {
        when(intercambioRepo.findById(1)).thenReturn(Optional.empty());

        ResenaIntercambio reseña = new ResenaIntercambio();
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.crear(1, "autor@test.com", reseña));
        assertEquals("Intercambio no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("crear() - error usuario no encontrado")
    void crear_ErrorUsuarioNoEncontrado() {
        when(intercambioRepo.findById(1)).thenReturn(Optional.of(intercambio));
        when(usuarioRepo.findByCorreo("autor@test.com")).thenReturn(Optional.empty());

        ResenaIntercambio reseña = new ResenaIntercambio();
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.crear(1, "autor@test.com", reseña));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("obtenerPorIntercambio() - devuelve lista de reseñas")
    void obtenerPorIntercambio_OK() {
        ResenaIntercambio r1 = new ResenaIntercambio();
        r1.setPuntuacion(4);
        r1.setIntercambio(intercambio);

        when(intercambioRepo.findById(1)).thenReturn(Optional.of(intercambio));
        when(reseñaRepo.findByIntercambio(intercambio)).thenReturn(List.of(r1));

        List<ResenaIntercambio> resultado = service.obtenerPorIntercambio(1);

        assertEquals(1, resultado.size());
        assertEquals(4, resultado.get(0).getPuntuacion());
    }

    @Test
    @DisplayName("calcularPromedio() - devuelve promedio correcto")
    void calcularPromedio_OK() {
        ResenaIntercambio r1 = new ResenaIntercambio();
        r1.setPuntuacion(4);
        r1.setIntercambio(intercambio);

        ResenaIntercambio r2 = new ResenaIntercambio();
        r2.setPuntuacion(5);
        r2.setIntercambio(intercambio);

        when(intercambioRepo.findById(1)).thenReturn(Optional.of(intercambio));
        when(reseñaRepo.findByIntercambio(intercambio)).thenReturn(List.of(r1, r2));

        double promedio = service.calcularPromedio(1);

        assertEquals(4.5, promedio);
    }

    @Test
    @DisplayName("calcularPromedio() - sin reseñas devuelve 0")
    void calcularPromedio_SinReseñas() {
        when(intercambioRepo.findById(1)).thenReturn(Optional.of(intercambio));
        when(reseñaRepo.findByIntercambio(intercambio)).thenReturn(List.of());

        double promedio = service.calcularPromedio(1);

        assertEquals(0.0, promedio);
    }
}

