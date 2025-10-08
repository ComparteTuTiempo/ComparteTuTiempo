package com.compartetutiempo.backend.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.compartetutiempo.backend.dto.IntercambioDTO;
import com.compartetutiempo.backend.model.Categoria;
import com.compartetutiempo.backend.model.Conversacion;
import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.IntercambioUsuario;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.model.enums.ModalidadServicio;
import com.compartetutiempo.backend.model.enums.TipoIntercambio;
import com.compartetutiempo.backend.repository.CategoriaRepository;
import com.compartetutiempo.backend.repository.IntercambioRepository;
import com.compartetutiempo.backend.repository.ReseñaIntercambioRepository;
import com.compartetutiempo.backend.repository.IntercambioUsuarioRepository;
import com.compartetutiempo.backend.repository.MensajeRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;
import com.compartetutiempo.backend.specifications.IntercambioSpecifications;

@Service
public class IntercambioService {

    private final IntercambioRepository intercambioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ReseñaIntercambioRepository resenaIntercambioRepository;
    private final IntercambioUsuarioRepository intercambioUsuarioRepository;
    private final MensajeRepository mensajeRepository;

    public IntercambioService(IntercambioRepository intercambioRepository,
        UsuarioRepository usuarioRepository,
        IntercambioUsuarioRepository intercambioUsuarioRepository,
        CategoriaRepository categoriaRepository ,
        ReseñaIntercambioRepository resenaIntercambioRepository
        ,MensajeRepository mensajeRepository) {
            this.intercambioRepository = intercambioRepository;
            this.usuarioRepository = usuarioRepository;
            this.intercambioUsuarioRepository = intercambioUsuarioRepository;
            this.categoriaRepository = categoriaRepository;
            this.resenaIntercambioRepository = resenaIntercambioRepository;
            this.mensajeRepository = mensajeRepository;
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

    public Intercambio actualizarIntercambio(Integer id, IntercambioDTO dto) {
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

    public IntercambioDTO obtenerPorId(Integer id) {
        Intercambio intercambio = intercambioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));
        List<IntercambioUsuario> participantes = intercambioUsuarioRepository.findByIntercambioId(id);
        return IntercambioDTO.fromEntity(intercambio, participantes);
    }

    public List<Intercambio> obtenerPorUsuario(Usuario user) {
        return intercambioRepository.findByUser(user);
    }

        @Transactional
    public IntercambioDTO solicitarIntercambio(Integer intercambioId, String correoDemandante) {
        Usuario demandante = usuarioRepository.findByCorreo(correoDemandante)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));

        // Validar que no sea el dueño
        if (intercambio.getUser().getId().equals(demandante.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El dueño no puede solicitar intercambio consigo mismo");
        }

        // Buscar si ya existe un intercambio activo para este usuario
        List<IntercambioUsuario> solicitudesActivas = intercambioUsuarioRepository
                .findActivosByIntercambioAndUsuario(intercambioId, demandante.getId());

        if (!solicitudesActivas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ya tienes una solicitud o intercambio pendiente en esta oferta");
        }

        // Crear nueva solicitud
        IntercambioUsuario iu = new IntercambioUsuario();
        iu.setIntercambio(intercambio);
        iu.setUsuario(demandante);
        iu.setEstado(EstadoIntercambio.EMPAREJAMIENTO);
        iu.setHorasAsignadas(intercambio.getNumeroHoras());

        intercambioUsuarioRepository.save(iu);

        List<IntercambioUsuario> participantes = intercambioUsuarioRepository.findByIntercambioId(intercambioId);
        return IntercambioDTO.fromEntity(intercambio, participantes);
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
    public void eliminarIntercambio(Integer id) {
        Intercambio intercambio = intercambioRepository.findById(id).orElse(null);
        if (intercambio == null) {
            throw new RuntimeException("Intercambio no encontrado");
        }
        List<IntercambioUsuario> participantes = intercambioUsuarioRepository.findByIntercambioId(intercambio.getId());
        
        for (IntercambioUsuario iu : participantes) {
            Conversacion conv = iu.getConversacion();
                if (conv != null) {
                mensajeRepository.deleteByConversacion(conv);
            }
        }

        resenaIntercambioRepository.deleteByIntercambio(intercambio);
        intercambioUsuarioRepository.deleteByIntercambio(intercambio);
        intercambioRepository.delete(intercambio);
    }
}
