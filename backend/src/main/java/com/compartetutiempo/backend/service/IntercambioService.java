package com.compartetutiempo.backend.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public IntercambioDTO avanzarEstado(Integer intercambioId, String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        IntercambioUsuario iu = intercambioUsuarioRepository
                .findByIntercambioIdAndUsuarioId(intercambioId, usuario.getId())
                .orElseThrow(() -> new RuntimeException("No eres participante de este intercambio"));

        EstadoIntercambio actual = iu.getEstado();
        EstadoIntercambio siguiente = switch (actual) {
            case EMPAREJAMIENTO -> EstadoIntercambio.CONSENSO;
            case CONSENSO -> EstadoIntercambio.EJECUCION;
            case EJECUCION -> EstadoIntercambio.FINALIZADO;
            default -> throw new IllegalStateException("El estado actual no permite más transiciones");
        };

        iu.setEstado(siguiente);
        intercambioUsuarioRepository.save(iu);

        Intercambio intercambio = iu.getIntercambio();
        List<IntercambioUsuario> participantes =
                intercambioUsuarioRepository.findByIntercambioId(intercambioId);

        return IntercambioDTO.fromEntity(intercambio, participantes);
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

        if (intercambio.getUser().getId().equals(demandante.getId())) {
            throw new IllegalStateException("El dueño no puede solicitar intercambio consigo mismo");
        }

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
