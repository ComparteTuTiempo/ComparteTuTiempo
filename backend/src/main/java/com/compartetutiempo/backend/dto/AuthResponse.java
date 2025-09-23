package com.compartetutiempo.backend.dto;

public class AuthResponse {

    private String token;

    // Constructor que recibe token
    public AuthResponse(String token) {
        this.token = token;
    }

    // Getter
    public String getToken() {
        return token;
    }

    // Setter (opcional si solo necesitas enviar)
    public void setToken(String token) {
        this.token = token;
    }
}
