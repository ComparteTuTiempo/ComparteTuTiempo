package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.model.Reporte;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoReporte;
import com.compartetutiempo.backend.repository.IntercambioRepository;
import com.compartetutiempo.backend.repository.ProductoRepository;
import com.compartetutiempo.backend.repository.ReporteRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final IntercambioRepository intercambioRepository;

    public ReporteService(ReporteRepository reporteRepository, UsuarioRepository usuarioRepository, ProductoRepository productoRepository, IntercambioRepository intercambioRepository) {
        this.reporteRepository = reporteRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.intercambioRepository = intercambioRepository;
    }

    public Reporte crearReporte(String correoReportador, String correoReportado, Reporte reporte) {
        Usuario reportador = usuarioRepository.findByCorreo(correoReportador)
                .orElseThrow(() -> new RuntimeException("Usuario reportador no encontrado"));
        Usuario reportado = usuarioRepository.findByCorreo(correoReportado)
                .orElseThrow(() -> new RuntimeException("Usuario reportado no encontrado"));

        reporte.setUsuarioReportador(reportador);
        reporte.setUsuarioReportado(reportado);

        return reporteRepository.save(reporte);
    }

    public List<Reporte> obtenerTodos() {
        return reporteRepository.findAll();
    }

    public List<Reporte> obtenerPendientes() {
        return reporteRepository.findByEstado(EstadoReporte.PENDIENTE);
    }

    public Reporte obtenerPorId(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
    }

    public Reporte confirmarReporte(Long id) {
        Reporte reporte = obtenerPorId(id);
        reporte.setEstado(EstadoReporte.CONFIRMADO);

        // Banear usuario
        Usuario reportado = reporte.getUsuarioReportado();

        // eliminar productos
        productoRepository.deleteAll(productoRepository.findByPropietario(reportado));

        // eliminar intercambios
        intercambioRepository.deleteAll(intercambioRepository.findByUser(reportado));

        // deshabilitar usuario
        reportado.setActivo(false);
        usuarioRepository.save(reportado);

        return reporteRepository.save(reporte);
    }

    public Reporte rechazarReporte(Long id) {
        Reporte reporte = obtenerPorId(id);
        reporte.setEstado(EstadoReporte.RECHAZADO);
        return reporteRepository.save(reporte);
    }
}
