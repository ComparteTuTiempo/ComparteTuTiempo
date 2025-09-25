package com.compartetutiempo.backend.repository;

import com.compartetutiempo.backend.model.Verificacion;
import com.compartetutiempo.backend.model.enums.EstadoVerificacion;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VerificacionRepository extends JpaRepository<Verificacion, Long> {
    List<Verificacion> findByEstado(EstadoVerificacion estado);
}
