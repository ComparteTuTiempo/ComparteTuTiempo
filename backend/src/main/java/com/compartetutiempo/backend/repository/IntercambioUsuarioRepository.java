package com.compartetutiempo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compartetutiempo.backend.model.IntercambioUsuario;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;

public interface IntercambioUsuarioRepository extends JpaRepository<IntercambioUsuario,Integer>{
    
    Optional<IntercambioUsuario> findByIntercambioIdAndUsuarioId(Integer intercambioId, Long usuarioId);

    List<IntercambioUsuario> findByIntercambioId(Integer intercambioId);

    List<IntercambioUsuario> findByUsuarioId(Long usuarioId);

    List<IntercambioUsuario> findByIntercambioUserAndEstado(Usuario ofertante, EstadoIntercambio estadoIntercambio);

}
