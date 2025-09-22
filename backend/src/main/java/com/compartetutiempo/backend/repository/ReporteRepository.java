package com.compartetutiempo.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.compartetutiempo.backend.model.Reporte;
import com.compartetutiempo.backend.model.enums.EstadoReporte;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
        List<Reporte> findByEstado(EstadoReporte estado);
}
