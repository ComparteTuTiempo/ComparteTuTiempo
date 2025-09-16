package com.compartetutiempo.backend.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.repository.IntercambioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

@Service
public class IntercambioService {

    private final IntercambioRepository intercambioRepository;
    private final UsuarioRepository usuarioRepository;

    public IntercambioService(IntercambioRepository intercambioRepository,UsuarioRepository usuarioRepository){
        this.intercambioRepository = intercambioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Intercambio crear(String correo, Intercambio intercambio) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        intercambio.setUser(usuario);
        intercambio.setFechaPublicacion(new Date());
        intercambio.setEstado(EstadoIntercambio.EMPAREJAMIENTO); 
        return intercambioRepository.save(intercambio);
    }

    public List<Intercambio> obtenerTodos() {
        return intercambioRepository.findAll();
    }
    public List<Intercambio> obtenerPorUsuario(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return intercambioRepository.findByUser(usuario);
    }

    public Intercambio actualizarIntercambio(Long id, Intercambio nuevosDatos) {
        Intercambio intercambio = intercambioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));

        intercambio.setNombre(nuevosDatos.getNombre() != null ? nuevosDatos.getNombre() : intercambio.getNombre());
        intercambio.setDescripcion(nuevosDatos.getDescripcion() != null ? nuevosDatos.getDescripcion() : intercambio.getDescripcion());
        intercambio.setNumeroHoras(nuevosDatos.getNumeroHoras() != null ? nuevosDatos.getNumeroHoras() : intercambio.getNumeroHoras());
        intercambio.setModalidad(nuevosDatos.getModalidad() != null ? nuevosDatos.getModalidad() : intercambio.getModalidad());
        intercambio.setEstado(nuevosDatos.getEstado() != null ? nuevosDatos.getEstado() : intercambio.getEstado());

        return intercambioRepository.save(intercambio);
    }

    public Intercambio obtenerPorId(Long id) {
        return intercambioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado con id: " + id));
    }

    
}
