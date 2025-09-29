package com.compartetutiempo.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.compartetutiempo.backend.dto.AcuerdoRequest;
import com.compartetutiempo.backend.dto.IntercambioDTO;
import com.compartetutiempo.backend.dto.IntercambioUsuarioDTO;

import com.compartetutiempo.backend.model.IntercambioUsuario;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.model.enums.TipoIntercambio;
import com.compartetutiempo.backend.repository.IntercambioUsuarioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

@Service
public class IntercambioUsuarioService {

    private final IntercambioUsuarioRepository intercambioUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConversacionService conversacionService;

    public IntercambioUsuarioService(IntercambioUsuarioRepository intercambioUsuarioRepository,
    UsuarioRepository usuarioRepository,ConversacionService conversacionService){
        this.intercambioUsuarioRepository = intercambioUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.conversacionService = conversacionService;
    }

    @Transactional
    public List<IntercambioUsuarioDTO> obtenerPorUsuarioOfertanteYEstado(String correo, EstadoIntercambio estado) {
         List<IntercambioUsuario> intercambios = intercambioUsuarioRepository.findByIntercambioUserCorreoAndEstado(correo, estado);

        return  intercambios.stream()
                .map(IntercambioUsuarioDTO::fromEntity)
                .toList();
    }

    @Transactional
    public IntercambioDTO aceptarSolicitud(Integer solicitudId, String correoOfertante) {
        IntercambioUsuario solicitud = intercambioUsuarioRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!solicitud.getIntercambio().getUser().getCorreo().equals(correoOfertante)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No tienes permiso para aceptar esta solicitud");
        } else if(solicitud.getIntercambio().getEstado() == EstadoIntercambio.CONSENSO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Esta oferta ya está en consenso");
        }

        List<String> correosParticipantes = List.of(
            solicitud.getUsuario().getCorreo(),
            correoOfertante
        );

        conversacionService.getOrCreateForIntercambioUsuario(
            solicitud.getId(), correosParticipantes
        );

        solicitud.setEstado(EstadoIntercambio.CONSENSO);
        intercambioUsuarioRepository.save(solicitud);

        return IntercambioDTO.fromEntity(
            solicitud.getIntercambio(),
            intercambioUsuarioRepository.findByIntercambioId(solicitud.getIntercambio().getId())
        );
    }

    @Transactional
    public IntercambioUsuarioDTO establecerAcuerdo(Integer id, AcuerdoRequest request, String correoUsuario) {
        IntercambioUsuario iu = intercambioUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("IntercambioUsuario no encontrado"));
        Usuario solicitante = iu.getUsuario();
        Usuario ofertante = iu.getIntercambio().getUser();

        if (!solicitante.getCorreo().equals(correoUsuario) &&
            !ofertante.getCorreo().equals(correoUsuario)) {
            throw new IllegalAccessError("No tienes permiso para establecer este acuerdo");
        }else if(solicitante.getNumeroHoras() < request.getHorasAsignadas()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El solicitante no dispone de suficientes horas para hacer este intercambio");
        }

        iu.setHorasAsignadas(request.getHorasAsignadas());
        iu.setTerminos(request.getTerminos());

        iu.setEstado(EstadoIntercambio.EJECUCION);

        intercambioUsuarioRepository.save(iu);

        return IntercambioUsuarioDTO.fromEntity(iu);
    }

    @Transactional
    public IntercambioUsuarioDTO finalizarAcuerdo(Integer id, String correoUsuario) {
        IntercambioUsuario iu = intercambioUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("IntercambioUsuario no encontrado"));

        Usuario usuarioSolicitante = iu.getUsuario();
        Usuario usuarioOfertante = iu.getIntercambio().getUser();
        TipoIntercambio tipoIntercambio = iu.getIntercambio().getTipo();
        // Validar que el usuario esté involucrado
        if (!iu.getUsuario().getCorreo().equals(correoUsuario) &&
            !iu.getIntercambio().getUser().getCorreo().equals(correoUsuario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No tienes permiso para finalizar este acuerdo");
        }

        if (iu.getEstado() != EstadoIntercambio.EJECUCION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El acuerdo no está en ejecución");
        }

        switch(tipoIntercambio){
            case OFERTA:
            //Si es oferta, el solicitante pierde horas y el ofertante gana
                usuarioSolicitante.setNumeroHoras(usuarioSolicitante.getNumeroHoras() - iu.getHorasAsignadas());
                usuarioOfertante.setNumeroHoras(usuarioOfertante.getNumeroHoras() + iu.getHorasAsignadas());
                break;
            case PETICION:
            //Si es peticion, el solicitante gana horas y el ofertante pierde
                usuarioSolicitante.setNumeroHoras(usuarioSolicitante.getNumeroHoras() + iu.getHorasAsignadas());
                usuarioOfertante.setNumeroHoras(usuarioOfertante.getNumeroHoras() - iu.getHorasAsignadas());
                break;
        }

        usuarioRepository.saveAll(List.of(usuarioSolicitante,usuarioOfertante));
        
        iu.setEstado(EstadoIntercambio.FINALIZADO);

        intercambioUsuarioRepository.save(iu);

        return IntercambioUsuarioDTO.fromEntity(iu);
    }
        
    

    @Transactional
    public void rechazarSolicitud(Integer solicitudId, String correoOfertante) {
        IntercambioUsuario solicitud = intercambioUsuarioRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!solicitud.getIntercambio().getUser().getCorreo().equals(correoOfertante)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No tienes permiso para rechazar esta solicitud");
        }

        intercambioUsuarioRepository.delete(solicitud);
    }
    
    @Transactional
    public List<IntercambioUsuarioDTO> obtenerSolicitudesPendientes(String correoOfertante) {
    Usuario ofertante = usuarioRepository.findByCorreo(correoOfertante)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<IntercambioUsuario> solicitudes = intercambioUsuarioRepository
            .findByIntercambioUserAndEstado(ofertante, EstadoIntercambio.EMPAREJAMIENTO);

        return solicitudes.stream()
            .map(IntercambioUsuarioDTO::fromEntity)
            .toList();
    }
}
