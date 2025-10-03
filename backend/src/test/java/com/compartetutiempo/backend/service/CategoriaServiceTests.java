package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.model.Categoria;
import com.compartetutiempo.backend.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Tecnología");
    }

    // -------------------------------
    // CREAR CATEGORÍA
    // -------------------------------
    @Test
    @DisplayName("crearCategoria - debe guardar y devolver categoría")
    void crearCategoria_OK() {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria result = categoriaService.crearCategoria(categoria);

        assertNotNull(result);
        assertEquals("Tecnología", result.getNombre());
        verify(categoriaRepository, times(1)).save(categoria);
    }

    // -------------------------------
    // LISTAR CATEGORÍAS
    // -------------------------------
    @Test
    @DisplayName("obtenerCategorias - devuelve lista de categorías")
    void obtenerCategorias_OK() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> result = categoriaService.obtenerCategorias();

        assertEquals(1, result.size());
        assertEquals("Tecnología", result.get(0).getNombre());
        verify(categoriaRepository, times(1)).findAll();
    }

    // -------------------------------
    // ACTUALIZAR CATEGORÍA
    // -------------------------------
    @Test
    @DisplayName("actualizarCategoria - categoría encontrada y actualizada")
    void actualizarCategoria_OK() {
        Categoria nueva = new Categoria();
        nueva.setNombre("Ciencia");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(i -> i.getArgument(0));

        Categoria result = categoriaService.actualizarCategoria(1L, nueva);

        assertEquals("Ciencia", result.getNombre());
        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    @DisplayName("actualizarCategoria - lanza excepción si no existe")
    void actualizarCategoria_NoEncontrada() {
        Categoria nueva = new Categoria();
        nueva.setNombre("Ciencia");

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                categoriaService.actualizarCategoria(99L, nueva)
        );

        assertEquals("Categoría no encontrada", ex.getMessage());
        verify(categoriaRepository, times(1)).findById(99L);
        verify(categoriaRepository, never()).save(any());
    }

    // -------------------------------
    // ELIMINAR CATEGORÍA
    // -------------------------------
    @Test
    @DisplayName("eliminarCategoria - elimina correctamente")
    void eliminarCategoria_OK() {
        doNothing().when(categoriaRepository).deleteById(1L);

        categoriaService.eliminarCategoria(1L);

        verify(categoriaRepository, times(1)).deleteById(1L);
    }
}

