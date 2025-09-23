package com.compartetutiempo.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compartetutiempo.backend.dto.ConversacionDTO;
import com.compartetutiempo.backend.model.Conversacion;
import com.compartetutiempo.backend.model.IntercambioUsuario;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.ConversacionRepository;
import com.compartetutiempo.backend.repository.IntercambioUsuarioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversacionService {
    private final ConversacionRepository conversacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final IntercambioUsuarioRepository intercambioUsuarioRepository;

    @Transactional
    public Conversacion createConversation(List<String> correos, String title) {
        List<Usuario> participantes = usuarioRepository.findByCorreoIn(correos);
        Conversacion conversacion = new Conversacion();
        conversacion.setTitulo(title);
        conversacion.setParticipantes(participantes);
        return conversacionRepository.save(conversacion);
    }

    @Transactional
    public List<Conversacion> getUserConversations(String correo) {
        return conversacionRepository.findByUserCorreo(correo);
    }

    public ConversacionDTO findByIntercambioUsuarioId(Integer intercambioUsuarioId){
        Optional<Conversacion> conversacion = conversacionRepository.findByIntercambioUsuarioId(intercambioUsuarioId);
        ConversacionDTO conversacionDTO = null;
        
        if(conversacion.isPresent()){
           conversacionDTO = ConversacionDTO.fromEntity(conversacion.get());
        }else{
            throw new RuntimeException("No se pudo encontrar la solicitud de intercambio");
        }
        
        return conversacionDTO;
    }

    @Transactional
    public Conversacion getById(Long id) {
        Optional<Conversacion> conversacion = conversacionRepository.findById(id);
        return conversacion.isPresent()?conversacion.get():null;
    }

    @Transactional
    public Conversacion getOrCreateForIntercambioUsuario(Integer intercambioUsuarioId, List<String> correos) {
        IntercambioUsuario iu = intercambioUsuarioRepository.findById(intercambioUsuarioId)
            .orElseThrow(() -> new RuntimeException("No se pudo encontrar la solicitud de intercambio"));

        if (iu.getConversacion() != null) {
            return iu.getConversacion();
        }

        List<Usuario> usuarios = usuarioRepository.findByCorreoIn(correos);
        if (usuarios.size() < 2) {
            throw new RuntimeException("Se requieren al menos dos usuarios para crear la conversación");
        }

        Conversacion conversacion = new Conversacion();
        conversacion.setParticipantes(usuarios);
        conversacion.setTitulo("Chat del intercambio: " + iu.getIntercambio().getNombre());

        conversacion = conversacionRepository.save(conversacion);
        
        iu.setConversacion(conversacion);
        intercambioUsuarioRepository.save(iu);

        return conversacion;
}


}

