package com.compartetutiempo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compartetutiempo.backend.model.Participacion;

public interface ParticipacionRepository extends JpaRepository<Participacion, Integer> {
    List<Participacion> findByEventoId(Integer eventoId);

    Optional<Participacion> findByEventoIdAndUsuarioCorreo(Integer eventoId, String correoParticipante);
}
