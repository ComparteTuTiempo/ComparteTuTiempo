package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.model.Verificacion;
import com.compartetutiempo.backend.model.enums.EstadoVerificacion;
import com.compartetutiempo.backend.repository.VerificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificacionServiceTest {

    @Mock
    private VerificacionRepository verificacionRepository;

    @InjectMocks
    private VerificacionService verificacionService;

    private Verificacion verificacion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        verificacion = new Verificacion();
        verificacion.setId(1L);
        verificacion.setDocumentoURL("doc.png");
        verificacion.setEstado(EstadoVerificacion.PENDIENTE);
    }

    @Test
    @DisplayName("crear() debe guardar con estado PENDIENTE")
    void crear_OK() {
        when(verificacionRepository.save(any(Verificacion.class))).thenReturn(verificacion);

        Verificacion creada = verificacionService.crear(new Verificacion());

        assertNotNull(creada);
        assertEquals(EstadoVerificacion.PENDIENTE, creada.getEstado());
        verify(verificacionRepository, times(1)).save(any(Verificacion.class));
    }

    @Test
    @DisplayName("obtenerPendientes() devuelve lista de verificaciones pendientes")
    void obtenerPendientes_OK() {
        when(verificacionRepository.findByEstado(EstadoVerificacion.PENDIENTE))
                .thenReturn(List.of(verificacion));

        List<Verificacion> pendientes = verificacionService.obtenerPendientes();

        assertFalse(pendientes.isEmpty());
        assertEquals(1, pendientes.size());
        assertEquals("doc.png", pendientes.get(0).getDocumentoURL());
        verify(verificacionRepository, times(1)).findByEstado(EstadoVerificacion.PENDIENTE);
    }

    @Test
    @DisplayName("obtenerPorId() devuelve la verificación si existe")
    void obtenerPorId_OK() {
        when(verificacionRepository.findById(1L)).thenReturn(Optional.of(verificacion));

        Verificacion encontrada = verificacionService.obtenerPorId(1L);

        assertNotNull(encontrada);
        assertEquals(1L, encontrada.getId());
        verify(verificacionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("obtenerPorId() lanza excepción si no existe")
    void obtenerPorId_NoEncontrado() {
        when(verificacionRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> verificacionService.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("Verificación no encontrada"));
        verify(verificacionRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("aprobar() cambia estado a APROBADA y guarda")
    void aprobar_OK() {
        when(verificacionRepository.save(verificacion)).thenReturn(verificacion);

        Verificacion aprobada = verificacionService.aprobar(verificacion);

        assertEquals(EstadoVerificacion.APROBADA, aprobada.getEstado());
        verify(verificacionRepository, times(1)).save(verificacion);
    }

    @Test
    @DisplayName("rechazar() cambia estado a RECHAZADA y guarda")
    void rechazar_OK() {
        when(verificacionRepository.save(verificacion)).thenReturn(verificacion);

        Verificacion rechazada = verificacionService.rechazar(verificacion);

        assertEquals(EstadoVerificacion.RECHAZADA, rechazada.getEstado());
        verify(verificacionRepository, times(1)).save(verificacion);
    }
}

