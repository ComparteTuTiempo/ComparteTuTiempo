package com.compartetutiempo.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.compartetutiempo.backend.model.Producto;
import com.compartetutiempo.backend.model.Usuario;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
        List<Producto> findByUser(Usuario user);
}
