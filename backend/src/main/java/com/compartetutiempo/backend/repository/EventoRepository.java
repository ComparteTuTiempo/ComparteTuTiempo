package com.compartetutiempo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compartetutiempo.backend.model.Evento;

public interface EventoRepository extends JpaRepository<Evento,Integer> {

    
}
