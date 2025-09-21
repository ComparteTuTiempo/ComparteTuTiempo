package com.compartetutiempo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compartetutiempo.backend.model.IntercambioUsuario;

public interface IntercambioUsuarioRepository extends JpaRepository<IntercambioUsuario,Integer>{
    
    Optional<IntercambioUsuario> findByIntercambioIdAndUsuarioId(Integer intercambioId, Long usuarioId);

    List<IntercambioUsuario> findByIntercambioId(Integer intercambioId);

    List<IntercambioUsuario> findByUsuarioId(Long usuarioId);

}
