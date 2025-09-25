package com.compartetutiempo.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.compartetutiempo.backend.dto.ConversacionDTO;
import com.compartetutiempo.backend.model.Conversacion;
import com.compartetutiempo.backend.service.ConversacionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/conversaciones")
@RequiredArgsConstructor
public class ConversacionController {
    
    private final ConversacionService conversationService;

    @PostMapping
    public ResponseEntity<Conversacion> createConversation(
            @RequestParam List<String> correos,
            @RequestParam(required = false) String title) {
        return ResponseEntity.ok(conversationService.createConversation(correos, title));
    }

    @GetMapping("/user/{correo}")
    public ResponseEntity<List<Conversacion>> getUserConversations(@PathVariable String correo) {
        List<Conversacion> conversaciones = conversationService.getUserConversations(correo);
        return ResponseEntity.ok(conversaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conversacion> getConversation(@PathVariable Long id) {
        Conversacion conversacion = conversationService.getById(id);
        if(conversacion != null){
            return new ResponseEntity<>(conversacion,HttpStatus.OK);
        }else{
            return new ResponseEntity<>(conversacion,HttpStatus.NOT_FOUND);
        }
        
    }

    @PostMapping("/intercambio-usuario/{intercambioUsuarioId}")
    public ResponseEntity<Conversacion> getOrCreateConversationForIntercambioUsuario(
            @PathVariable Integer intercambioUsuarioId,
            @RequestParam List<String> correos) {
        return ResponseEntity.ok(conversationService.getOrCreateForIntercambioUsuario(intercambioUsuarioId, correos));
    }

    @GetMapping("/intercambio-usuario/{intercambioUsuarioId}")
    public ResponseEntity<ConversacionDTO> getConversationForIntercambioUsuario(@PathVariable Integer intercambioUsuarioId) {
        return ResponseEntity.ok(conversationService.findByIntercambioUsuarioId(intercambioUsuarioId));
    }

}

