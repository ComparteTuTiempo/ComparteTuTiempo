package com.compartetutiempo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compartetutiempo.backend.model.Notificacion;
import com.compartetutiempo.backend.model.Usuario;

public interface NotificacionRepository extends JpaRepository<Notificacion,Integer>{
    
    List<Notificacion> findByUsuarioDestinoOrderByTimestampDesc(Usuario usuario);

    Optional<Notificacion> findById(Integer id);
}
