package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.model.Reporte;
import com.compartetutiempo.backend.service.ReporteService;
import com.compartetutiempo.backend.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:3000")
public class ReporteController {

    private final ReporteService reporteService;
    private final UsuarioService usuarioService;

    public ReporteController(ReporteService reporteService, UsuarioService usuarioService) {
        this.reporteService = reporteService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/{correoReportado}")
    public ResponseEntity<Reporte> crearReporte(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String correoReportado,
            @RequestBody Reporte reporte) {

        String correoReportador = jwt.getSubject();
        Reporte nuevo = reporteService.crearReporte(correoReportador, correoReportado, reporte);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping
    public ResponseEntity<List<Reporte>> obtenerTodos() {
        return ResponseEntity.ok(reporteService.obtenerTodos());
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Reporte>> obtenerPendientes() {
        return ResponseEntity.ok(reporteService.obtenerPendientes());
    }

    // Obtener detalle de un reporte
    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerReporte(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.obtenerPorId(id));
    }

    // Confirmar reporte -> se banea usuario reportado
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<Reporte> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.confirmarReporte(id));
    }

    // Rechazar reporte
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<Reporte> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.rechazarReporte(id));
    }
}
