package com.compartetutiempo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;

import java.util.List;
import java.util.Optional;


public interface IntercambioRepository extends JpaRepository<Intercambio, Long>{

    Optional<Intercambio> findById(Integer id);

    List<Intercambio> findByUser(Usuario user);
    
    List<Intercambio> findByUserAndEstado(Usuario user, EstadoIntercambio estado);
}
