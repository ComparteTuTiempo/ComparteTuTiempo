package com.compartetutiempo.backend.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.compartetutiempo.backend.dto.IntercambioDTO;
import com.compartetutiempo.backend.model.Categoria;
import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.repository.CategoriaRepository;
import com.compartetutiempo.backend.repository.IntercambioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

@Service
public class IntercambioService {

    private final IntercambioRepository intercambioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public IntercambioService(IntercambioRepository intercambioRepository, UsuarioRepository usuarioRepository, CategoriaRepository categoriaRepository) {
        this.intercambioRepository = intercambioRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

public Intercambio crear(String correo, IntercambioDTO dto) {
    Usuario usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    Intercambio intercambio = new Intercambio();
    intercambio.setNombre(dto.getNombre());
    intercambio.setDescripcion(dto.getDescripcion());
    intercambio.setNumeroHoras(dto.getNumeroHoras());
    intercambio.setTipo(dto.getTipo());
    intercambio.setModalidad(dto.getModalidad());
    intercambio.setUser(usuario);
    intercambio.setFechaPublicacion(new Date());
    intercambio.setEstado(EstadoIntercambio.EMPAREJAMIENTO);

    // 🔹 Buscar categorías por id
    List<Categoria> categorias = categoriaRepository.findAllById(dto.getCategorias());
    intercambio.setCategorias(new HashSet<>(categorias));

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
        intercambio.setDescripcion(
                nuevosDatos.getDescripcion() != null ? nuevosDatos.getDescripcion() : intercambio.getDescripcion());
        intercambio.setNumeroHoras(
                nuevosDatos.getNumeroHoras() != null ? nuevosDatos.getNumeroHoras() : intercambio.getNumeroHoras());
        intercambio.setModalidad(
                nuevosDatos.getModalidad() != null ? nuevosDatos.getModalidad() : intercambio.getModalidad());
        intercambio.setEstado(nuevosDatos.getEstado() != null ? nuevosDatos.getEstado() : intercambio.getEstado());

        return intercambioRepository.save(intercambio);
    }

    public Intercambio obtenerPorId(Long id) {
        return intercambioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado con id: " + id));
    }

    public List<Intercambio> obtenerPorUsuario(Usuario user) {
        return intercambioRepository.findByUser(user);
    }

    public List<Intercambio> obtenerHistorial(Usuario user) {
        return intercambioRepository.findByUserAndEstado(user, EstadoIntercambio.FINALIZADO);
    }
}
