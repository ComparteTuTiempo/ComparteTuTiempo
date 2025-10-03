package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.model.ResenaIntercambio;
import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.ReseñaIntercambioRepository;
import com.compartetutiempo.backend.repository.IntercambioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ResenaIntercambioService {

    private final ReseñaIntercambioRepository reseñaRepo;
    private final IntercambioRepository intercambioRepo;
    private final UsuarioRepository usuarioRepo;

    public ResenaIntercambioService(ReseñaIntercambioRepository reseñaRepo, IntercambioRepository intercambioRepo, UsuarioRepository usuarioRepo) {
        this.reseñaRepo = reseñaRepo;
        this.intercambioRepo = intercambioRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public ResenaIntercambio crear(Integer intercambioId, String correoAutor, ResenaIntercambio reseña) {
        Intercambio intercambio = intercambioRepo.findById(intercambioId)
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));
        Usuario autor = usuarioRepo.findByCorreo(correoAutor)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        reseña.setIntercambio(intercambio);
        reseña.setAutor(autor);

        return reseñaRepo.save(reseña);
    }

    public List<ResenaIntercambio> obtenerPorIntercambio(Integer intercambioId) {
        Intercambio intercambio = intercambioRepo.findById(intercambioId)
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));
        return reseñaRepo.findByIntercambio(intercambio);
    }

    public double calcularPromedio(Integer intercambioId) {
        List<ResenaIntercambio> reseñas = obtenerPorIntercambio(intercambioId);
        if (reseñas.isEmpty()) return 0.0;
        return reseñas.stream().mapToInt(ResenaIntercambio::getPuntuacion).average().orElse(0.0);
    }
}
