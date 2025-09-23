package com.compartetutiempo.backend.service;

import com.compartetutiempo.backend.model.Verificacion;
import com.compartetutiempo.backend.model.enums.EstadoVerificacion;
import com.compartetutiempo.backend.repository.VerificacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VerificacionService {

    private final VerificacionRepository verificacionRepository;

    public VerificacionService(VerificacionRepository verificacionRepository) {
        this.verificacionRepository = verificacionRepository;
    }

    public Verificacion crear(Verificacion verificacion) {
        verificacion.setEstado(EstadoVerificacion.PENDIENTE);
        return verificacionRepository.save(verificacion);
    }

    public List<Verificacion> obtenerPendientes() {
        return verificacionRepository.findByEstado(EstadoVerificacion.PENDIENTE);
    }

    public Verificacion obtenerPorId(Long id) {
        return verificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Verificación no encontrada con id " + id));
    }

    public Verificacion aprobar(Verificacion verificacion) {
        verificacion.setEstado(EstadoVerificacion.APROBADA);
        return verificacionRepository.save(verificacion);
    }

    public Verificacion rechazar(Verificacion verificacion) {
        verificacion.setEstado(EstadoVerificacion.RECHAZADA);
        return verificacionRepository.save(verificacion);
    }
}
