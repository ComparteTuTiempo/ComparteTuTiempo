package com.compartetutiempo.backend.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.compartetutiempo.backend.dto.IntercambioDTO;
import com.compartetutiempo.backend.model.Categoria;
import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.model.enums.ModalidadServicio;
import com.compartetutiempo.backend.model.enums.TipoIntercambio;
import com.compartetutiempo.backend.repository.CategoriaRepository;
import com.compartetutiempo.backend.repository.IntercambioRepository;
import com.compartetutiempo.backend.repository.ReseñaIntercambioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;
import com.compartetutiempo.backend.specifications.IntercambioSpecifications;

import jakarta.transaction.Transactional;

@Service
public class IntercambioService {

    private final IntercambioRepository intercambioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ReseñaIntercambioRepository resenaIntercambioRepository;

    public IntercambioService(IntercambioRepository intercambioRepository, UsuarioRepository usuarioRepository , ReseñaIntercambioRepository resenaIntercambioRepository,
            CategoriaRepository categoriaRepository) {
        this.intercambioRepository = intercambioRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.resenaIntercambioRepository = resenaIntercambioRepository;
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

    public Intercambio actualizarIntercambio(Long id, IntercambioDTO dto) {
        Intercambio intercambio = intercambioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));

        intercambio.setNombre(dto.getNombre() != null ? dto.getNombre() : intercambio.getNombre());
        intercambio.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion() : intercambio.getDescripcion());
        intercambio.setNumeroHoras(dto.getNumeroHoras() != null ? dto.getNumeroHoras() : intercambio.getNumeroHoras());
        intercambio.setModalidad(dto.getModalidad() != null ? dto.getModalidad() : intercambio.getModalidad());
        intercambio.setTipo(dto.getTipo() != null ? dto.getTipo() : intercambio.getTipo());

        if (dto.getCategorias() != null) {
            if (dto.getCategorias() != null) {
                List<Categoria> categorias = categoriaRepository.findAllById(dto.getCategorias());
                intercambio.setCategorias(new HashSet<>(categorias)); // 👈 convierte a Set
            }
        }
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

    public List<Intercambio> filtrar(
            TipoIntercambio tipo,
            ModalidadServicio modalidad,
            List<Long> categoriaIds,
            Double minHoras,
            Double maxHoras,
            String q) {
        Specification<Intercambio> spec = Specification
                .where(IntercambioSpecifications.conTipo(tipo))
                .and(IntercambioSpecifications.conModalidad(modalidad))
                .and(IntercambioSpecifications.conCategoriaIds(categoriaIds))
                .and(IntercambioSpecifications.conHorasMin(minHoras))
                .and(IntercambioSpecifications.conHorasMax(maxHoras))
                .and(IntercambioSpecifications.conTexto(q));

        return intercambioRepository.findAll(spec); // ← ahora sí existe
    }

    @Transactional
    public void eliminarIntercambio(Long id) {
        resenaIntercambioRepository.deleteByIntercambioId(id);
        if (!intercambioRepository.existsById(id)) {
            throw new RuntimeException("Intercambio no encontrado");
        }
        intercambioRepository.deleteById(id);
    }
}
