package com.compartetutiempo.backend.repository;

import com.compartetutiempo.backend.model.Resena;
import com.compartetutiempo.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByDestinatario(Usuario destinatario);
    boolean existsByAutorAndDestinatario(Usuario autor, Usuario destinatario);
}
