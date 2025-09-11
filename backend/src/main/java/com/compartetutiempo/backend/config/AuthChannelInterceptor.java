package com.compartetutiempo.backend.config;

import com.compartetutiempo.backend.config.JwtService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    public AuthChannelInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            // Evitar re-procesar si ya hay token guardado
            if (!accessor.getSessionAttributes().containsKey("token")) {

                String authHeader = accessor.getFirstNativeHeader("Authorization");
                String token = null;

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }

                // Validar token y guardarlo en sessionAttributes
                if (token != null && jwtService.validateToken(token)) {
                    accessor.getSessionAttributes().put("token", token);
                    System.out.println("✅ Token válido guardado en sesión");
                } else {
                    System.out.println("❌ Token inválido en CONNECT");
                }
            }
        }

    return message;
}



}


