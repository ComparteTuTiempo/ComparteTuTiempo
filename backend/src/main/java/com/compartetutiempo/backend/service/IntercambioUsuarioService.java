package com.compartetutiempo.backend.service;

import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compartetutiempo.backend.dto.IntercambioDTO;
import com.compartetutiempo.backend.dto.IntercambioUsuarioDTO;

import com.compartetutiempo.backend.model.IntercambioUsuario;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;

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
    public IntercambioDTO aceptarSolicitud(Integer solicitudId, String correoOfertante) {
        IntercambioUsuario solicitud = intercambioUsuarioRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!solicitud.getIntercambio().getUser().getCorreo().equals(correoOfertante)) {
            throw new RuntimeException("No tienes permiso para aceptar esta solicitud");
        } else if(solicitud.getIntercambio().getEstado() == EstadoIntercambio.CONSENSO) {
            throw new RuntimeException("Esta oferta ya está en consenso");
        }

        // Creamos o recuperamos la conversación
        List<String> correosParticipantes = List.of(
            solicitud.getUsuario().getCorreo(),
            correoOfertante
        );

        conversacionService.getOrCreateForIntercambioUsuario(
            solicitud.getId(), correosParticipantes
        );

        // Cambiamos el estado
        solicitud.setEstado(EstadoIntercambio.CONSENSO);
        intercambioUsuarioRepository.save(solicitud);

        return IntercambioDTO.fromEntity(
            solicitud.getIntercambio(),
            intercambioUsuarioRepository.findByIntercambioId(solicitud.getIntercambio().getId())
        );
    }


        
    

    @Transactional
    public void rechazarSolicitud(Integer solicitudId, String correoOfertante) {
        IntercambioUsuario solicitud = intercambioUsuarioRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!solicitud.getIntercambio().getUser().getCorreo().equals(correoOfertante)) {
            throw new RuntimeException("No tienes permiso para rechazar esta solicitud");
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
