package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.Verificacion;
import com.compartetutiempo.backend.model.enums.EstadoVerificacion;
import com.compartetutiempo.backend.service.StorageService;
import com.compartetutiempo.backend.service.UsuarioService;
import com.compartetutiempo.backend.service.VerificacionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/verificaciones")
public class VerificacionController {

    private final VerificacionService verificacionService;
    private final UsuarioService usuarioService;
    private final StorageService storageService;

    public VerificacionController(VerificacionService verificacionService, UsuarioService usuarioService , StorageService storageService) {
        this.verificacionService = verificacionService;
        this.usuarioService = usuarioService;
        this.storageService = storageService;
    }

    /**
     * Crear petición de verificación por parte del usuario
     */
@PostMapping("/{correo}")
public ResponseEntity<Verificacion> crearSolicitud(
        @PathVariable String correo,
        @RequestParam("documentoURL") MultipartFile documentoURL) throws IOException {

    // Obtenemos el usuario por correo
    Usuario usuario = usuarioService.obtenerPorCorreo(correo);

    if (usuario == null) {
        return ResponseEntity.badRequest().build();
    }

    // Creamos la verificación
    Verificacion verificacion = new Verificacion();
    verificacion.setUsuario(usuario);
    verificacion.setEstado(EstadoVerificacion.PENDIENTE);

    // Guardamos la foto en local (carpeta uploads)
    String nombreArchivo = documentoURL.getOriginalFilename();
    Path path = Paths.get("uploads/" + nombreArchivo);
    Files.createDirectories(path.getParent());
    Files.write(path, documentoURL.getBytes());

    verificacion.setDocumentoURL(nombreArchivo);

    Verificacion creada = verificacionService.crear(verificacion);
    return ResponseEntity.ok(creada);
}



    /**
     * Listar todas las verificaciones pendientes (solo ADMIN)
     */
    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Verificacion>> obtenerPendientes() {
        return ResponseEntity.ok(verificacionService.obtenerPendientes());
    }

    /**
     * Aprobar una verificación (solo ADMIN)
     */
    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Verificacion> aprobar(@PathVariable Long id) {
        Verificacion verificacion = verificacionService.obtenerPorId(id);
        verificacion.setEstado(EstadoVerificacion.APROBADA);

        // actualizar usuario
        Usuario user = verificacion.getUsuario();
        user.setVerificado(true);
        usuarioService.guardar(user); // <<-- usa tu método real para persistir el usuario

        return ResponseEntity.ok(verificacionService.aprobar(verificacion));
    }

    /**
     * Rechazar una verificación (solo ADMIN)
     */
    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Verificacion> rechazar(@PathVariable Long id) {
        Verificacion verificacion = verificacionService.obtenerPorId(id);
        verificacion.setEstado(EstadoVerificacion.RECHAZADA);
        return ResponseEntity.ok(verificacionService.rechazar(verificacion));
    }
}
