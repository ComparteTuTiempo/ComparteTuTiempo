package com.compartetutiempo.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compartetutiempo.backend.model.Conversacion;
import com.compartetutiempo.backend.model.Mensaje;


public interface MensajeRepository extends JpaRepository<Mensaje, Long>{

    List<Mensaje> findByConversacionIdOrderByTimestampAsc(Long conversacionId);

    void deleteByConversacion(Conversacion conversacion);

}
