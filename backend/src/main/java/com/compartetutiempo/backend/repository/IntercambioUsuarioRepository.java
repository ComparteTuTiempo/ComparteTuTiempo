package com.compartetutiempo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.IntercambioUsuario;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;

public interface IntercambioUsuarioRepository extends JpaRepository<IntercambioUsuario,Integer>{
    
    @Query("SELECT iu FROM IntercambioUsuario iu " +
       "WHERE iu.intercambio.id = :intercambioId " +
       "AND iu.usuario.id = :usuarioId " +
       "AND iu.estado <> com.compartetutiempo.backend.model.enums.EstadoIntercambio.FINALIZADO")
    List<IntercambioUsuario> findActivosByIntercambioAndUsuario(
        @Param("intercambioId") Integer intercambioId,
        @Param("usuarioId") Long usuarioId
    );


    List<IntercambioUsuario> findByIntercambioId(Integer intercambioId);

    List<IntercambioUsuario> findByUsuarioId(Long usuarioId);

    List<IntercambioUsuario> findByIntercambioUserAndEstado(Usuario ofertante, EstadoIntercambio estadoIntercambio);

    List<IntercambioUsuario> findByIntercambioUserCorreoAndEstado(String correoOfertante, EstadoIntercambio estado);

    Optional<IntercambioUsuario> findByIntercambioIdAndUsuarioCorreoAndEstado(Integer intercambioId,String correo,
        EstadoIntercambio estado);

    void deleteByIntercambio(Intercambio intercambio);
    
    void deleteByUsuario(Usuario usuario);

}
