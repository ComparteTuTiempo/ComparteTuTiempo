package com.compartetutiempo.backend.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.compartetutiempo.backend.dto.IntercambioDTO;
import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.IntercambioUsuario;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.repository.IntercambioRepository;
import com.compartetutiempo.backend.repository.IntercambioUsuarioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

@Service
public class IntercambioService {

    private final IntercambioRepository intercambioRepository;
    private final UsuarioRepository usuarioRepository;
    private final IntercambioUsuarioRepository intercambioUsuarioRepository;

    public IntercambioService(IntercambioRepository intercambioRepository,
    UsuarioRepository usuarioRepository,
    IntercambioUsuarioRepository intercambioUsuarioRepository){
        this.intercambioRepository = intercambioRepository;
        this.usuarioRepository = usuarioRepository;
        this.intercambioUsuarioRepository = intercambioUsuarioRepository;
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

    
}
