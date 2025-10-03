package com.compartetutiempo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compartetutiempo.backend.model.Producto;
import com.compartetutiempo.backend.model.ProductoUsuario;
import com.compartetutiempo.backend.model.Usuario;

public interface ProductoUsuarioRepository extends JpaRepository<ProductoUsuario,Integer>{

    List<ProductoUsuario> findByProductoPropietarioOrComprador(Usuario propietario, Usuario comprador);

    Optional<ProductoUsuario> findByProductoPropietarioAndComprador(Usuario propietario, Usuario comprador);
    
    Optional<ProductoUsuario> findById(Integer id);

    void deleteByProducto(Producto producto);
    
    void deleteByComprador(Usuario comprador);
    
}
