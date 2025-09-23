package com.compartetutiempo.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.compartetutiempo.backend.model.Mensaje;
import com.compartetutiempo.backend.service.MensajeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mensajes")
@RequiredArgsConstructor
public class MensajeController {
    private final MensajeService chatMessageService;

    @PostMapping("/{conversationId}")
    public ResponseEntity<Mensaje> sendMessage(
            @PathVariable Long conversationId,
            @RequestParam String senderCorreo,
            @RequestBody String content) {
        return ResponseEntity.ok(chatMessageService.sendMessage(conversationId, senderCorreo, content));
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<List<Mensaje>> getMessages(@PathVariable Long conversationId) {
        return ResponseEntity.ok(chatMessageService.getMessages(conversationId));
    }
}
