package com.compartetutiempo.backend.repository;

import com.compartetutiempo.backend.model.ResenaIntercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.Intercambio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReseñaIntercambioRepository extends JpaRepository<ResenaIntercambio, Long> {
    List<ResenaIntercambio> findByIntercambio(Intercambio intercambio);
    
    void deleteByIntercambioId(Integer intercambioId);

    void deleteByIntercambio(Intercambio intercambio);
    
    void deleteByAutor(Usuario autor);
}
