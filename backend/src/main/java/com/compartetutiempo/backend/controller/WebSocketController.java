package com.compartetutiempo.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/send")
    public void sendMessage(@Payload String message) {
        messagingTemplate.convertAndSend("/topic/messages", message);
    }

    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload String message) {
        messagingTemplate.convertAndSendToUser("userId", "/queue/private", message);
    }
}