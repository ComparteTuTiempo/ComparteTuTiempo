package com.compartetutiempo.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.compartetutiempo.backend.model.Producto;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoProducto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
        List<Producto> findByPropietario(Usuario user);

        List<Producto> findByPropietarioAndEstado(Usuario propietario, EstadoProducto estado); 
}
