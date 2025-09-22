package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.model.Resena;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.repository.ResenaRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResenaService {
    private final ResenaRepository resenaRepository;
    private final UsuarioRepository usuarioRepository;

    public ResenaService(ResenaRepository resenaRepository, UsuarioRepository usuarioRepository) {
        this.resenaRepository = resenaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Resena crearResena(String correoAutor, String correoDestinatario, int puntuacion, String comentario) {
        Usuario autor = usuarioRepository.findByCorreo(correoAutor).orElseThrow();
        Usuario destinatario = usuarioRepository.findByCorreo(correoDestinatario).orElseThrow();

        if (autor.getCorreo().equals(destinatario.getCorreo())) {
            throw new IllegalArgumentException("No puedes dejarte reseñas a ti mismo");
        }

        if (resenaRepository.existsByAutorAndDestinatario(autor, destinatario)) {
            throw new IllegalArgumentException("Ya has dejado una reseña a este usuario");
        }

        Resena resena = new Resena();
        resena.setAutor(autor);
        resena.setDestinatario(destinatario);
        resena.setPuntuacion(puntuacion);
        resena.setComentario(comentario);

        return resenaRepository.save(resena);
    }

    public List<Resena> obtenerResenas(String correoDestinatario) {
        Usuario destinatario = usuarioRepository.findByCorreo(correoDestinatario).orElseThrow();
        return resenaRepository.findByDestinatario(destinatario);
    }

    public double calcularPromedio(String correoDestinatario) {
        List<Resena> resenas = obtenerResenas(correoDestinatario);
        if (resenas.isEmpty()) return 0.0;
        return resenas.stream().mapToInt(Resena::getPuntuacion).average().orElse(0.0);
    }
}
