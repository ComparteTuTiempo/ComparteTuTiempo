package com.compartetutiempo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.compartetutiempo.backend.model.Conversacion;

@Repository
public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {
    @Query("SELECT c FROM Conversacion c JOIN c.participantes p WHERE p.correo = :correo")
    List<Conversacion> findByUserCorreo(@Param("correo") String correo);

    @Query("SELECT c FROM Conversacion c JOIN IntercambioUsuario iu ON iu.conversacion = c WHERE iu.id = :intercambioUsuarioId")
    Optional<Conversacion> findByIntercambioUsuarioId(@Param("intercambioUsuarioId") Integer intercambioUsuarioId);

}