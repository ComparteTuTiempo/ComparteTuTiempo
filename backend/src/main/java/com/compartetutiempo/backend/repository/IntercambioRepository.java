package com.compartetutiempo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;


import java.util.List;


public interface IntercambioRepository extends JpaRepository<Intercambio, Long>, JpaSpecificationExecutor<Intercambio>{

    List<Intercambio> findByUser(Usuario user);
    
    List<Intercambio> findByUserAndEstado(Usuario user, EstadoIntercambio estado);
}
