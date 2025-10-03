package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.Reporte;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoReporte;
import com.compartetutiempo.backend.repository.IntercambioRepository;
import com.compartetutiempo.backend.repository.ProductoRepository;
import com.compartetutiempo.backend.repository.ReporteRepository;
import com.compartetutiempo.backend.repository.ReseñaIntercambioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock private ReporteRepository reporteRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private IntercambioRepository intercambioRepository;
    @Mock private ReseñaIntercambioRepository resenaRepository;

    @InjectMocks
    private ReporteService reporteService;

    private Usuario reportador;
    private Usuario reportado;
    private Reporte reporte;

    @BeforeEach
    void setUp() {
        reportador = new Usuario();
        reportador.setId(1L);
        reportador.setCorreo("reportador@test.com");
        reportador.setActivo(true);

        reportado = new Usuario();
        reportado.setId(2L);
        reportado.setCorreo("reportado@test.com");
        reportado.setActivo(true);

        reporte = new Reporte();
        reporte.setId(100);
        reporte.setTitulo("Spam");
        reporte.setDescripcion("Hace spam");
        reporte.setEstado(EstadoReporte.PENDIENTE);
        reporte.setUsuarioReportador(reportador);
        reporte.setUsuarioReportado(reportado);
    }

    @Test
    @DisplayName("crearReporte debe guardar correctamente")
    void crearReporte_OK() {
        when(usuarioRepository.findByCorreo("reportador@test.com")).thenReturn(Optional.of(reportador));
        when(usuarioRepository.findByCorreo("reportado@test.com")).thenReturn(Optional.of(reportado));
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        Reporte result = reporteService.crearReporte("reportador@test.com", "reportado@test.com", new Reporte());

        assertThat(result).isNotNull();
        assertThat(result.getUsuarioReportador()).isEqualTo(reportador);
        assertThat(result.getUsuarioReportado()).isEqualTo(reportado);
        verify(reporteRepository).save(any(Reporte.class));
    }

    @Test
    @DisplayName("obtenerTodos debe devolver lista de reportes")
    void obtenerTodos_OK() {
        when(reporteRepository.findAll()).thenReturn(List.of(reporte));

        List<Reporte> result = reporteService.obtenerTodos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitulo()).isEqualTo("Spam");
    }

    @Test
    @DisplayName("obtenerPendientes debe devolver solo los pendientes")
    void obtenerPendientes_OK() {
        when(reporteRepository.findByEstado(EstadoReporte.PENDIENTE)).thenReturn(List.of(reporte));

        List<Reporte> result = reporteService.obtenerPendientes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isEqualTo(EstadoReporte.PENDIENTE);
    }

    @Test
    @DisplayName("obtenerPorId existente devuelve reporte")
    void obtenerPorId_OK() {
        when(reporteRepository.findById(100L)).thenReturn(Optional.of(reporte));

        Reporte result = reporteService.obtenerPorId(100L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("obtenerPorId inexistente lanza excepción")
    void obtenerPorId_NoEncontrado() {
        when(reporteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reporteService.obtenerPorId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Reporte no encontrado");
    }

    @Test
    @DisplayName("confirmarReporte debe banear usuario, borrar productos, reseñas e intercambios")
    void confirmarReporte_OK() {
        when(reporteRepository.findById(100L)).thenReturn(Optional.of(reporte));
        when(intercambioRepository.findByUser(reportado)).thenReturn(List.of(new Intercambio()));
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        Reporte result = reporteService.confirmarReporte(100L);

        assertThat(result.getEstado()).isEqualTo(EstadoReporte.CONFIRMADO);
        verify(productoRepository).deleteAll(anyList());
        verify(intercambioRepository).deleteAll(anyList());
        verify(usuarioRepository).save(reportado);
        assertThat(reportado.isActivo()).isFalse();
    }

    @Test
    @DisplayName("rechazarReporte debe cambiar estado a RECHAZADO")
    void rechazarReporte_OK() {
        when(reporteRepository.findById(100L)).thenReturn(Optional.of(reporte));
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        Reporte result = reporteService.rechazarReporte(100L);

        assertThat(result.getEstado()).isEqualTo(EstadoReporte.RECHAZADO);
        verify(reporteRepository).save(reporte);
    }
}

