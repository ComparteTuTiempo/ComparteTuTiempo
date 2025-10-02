package com.compartetutiempo.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.compartetutiempo.backend.dto.AcuerdoRequest;
import com.compartetutiempo.backend.dto.IntercambioDTO;
import com.compartetutiempo.backend.dto.IntercambioUsuarioDTO;
import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.model.enums.ModalidadServicio;
import com.compartetutiempo.backend.model.enums.Role;
import com.compartetutiempo.backend.model.enums.TipoIntercambio;
import com.compartetutiempo.backend.model.enums.TipoNotificacion;
import com.compartetutiempo.backend.service.IntercambioService;
import com.compartetutiempo.backend.service.IntercambioUsuarioService;
import com.compartetutiempo.backend.service.NotificacionService;
import com.compartetutiempo.backend.service.UsuarioService;

@RestController
@RequestMapping("/intercambios")
public class IntercambioController {

    private final IntercambioService intercambioService;

    private final UsuarioService usuarioService;

    private final IntercambioUsuarioService intercambioUsuarioService;

    private final NotificacionService notificacionService;



    public IntercambioController(IntercambioService intercambioService, UsuarioService usuarioService
    , IntercambioUsuarioService intercambioUsuarioService,NotificacionService notificacionService ){
        this.intercambioService = intercambioService;
        this.usuarioService = usuarioService;
        this.intercambioUsuarioService = intercambioUsuarioService;
        this.notificacionService = notificacionService;
    }

    @PostMapping("/{correo}")
    public ResponseEntity<Intercambio> crear(
            @PathVariable String correo,
            @RequestBody IntercambioDTO dto) {

        Intercambio creado = intercambioService.crear(correo, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<Intercambio>> obtenerTodos() {
        List<Intercambio> intercambios = intercambioService.obtenerTodos();
        return ResponseEntity.ok(intercambios);
    }

    @GetMapping("/usuario/{correo}")
    public ResponseEntity<List<Intercambio>> obtenerIntercambiosPorUsuario(@PathVariable String correo) {
        List<Intercambio> intercambios = intercambioService.obtenerPorUsuario(correo);
        return ResponseEntity.ok(intercambios);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<IntercambioUsuarioDTO>> getByEstado(
            @PathVariable EstadoIntercambio estado,
            @AuthenticationPrincipal Jwt jwt) {

        String correo = jwt.getSubject(); 
        List<IntercambioUsuarioDTO> intercambios = intercambioUsuarioService. obtenerPorUsuarioOfertanteYEstado(correo, estado);

        return ResponseEntity.ok(intercambios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Intercambio> actualizarIntercambio(
            @PathVariable Integer id,
            @RequestBody IntercambioDTO dto) {
        Intercambio actualizado = intercambioService.actualizarIntercambio(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntercambioDTO> obtenerIntercambio(@PathVariable Integer id) {
        IntercambioDTO intercambio = intercambioService.obtenerPorId(id);
        return ResponseEntity.ok(intercambio);
    }


    
    @GetMapping("/usuario")
    public ResponseEntity<List<Intercambio>> obtenerPorUsuario(@AuthenticationPrincipal Jwt jwt) {
        
        String correo = jwt.getSubject(); 
        Usuario user = usuarioService.obtenerPorCorreo(correo);

        List<Intercambio> intercambios = intercambioService.obtenerPorUsuario(user);
        return ResponseEntity.ok(intercambios);
    }

    @PostMapping("/{id}/solicitar")
    public ResponseEntity<IntercambioDTO> solicitarIntercambio(
    @PathVariable Integer id,
    @AuthenticationPrincipal Jwt jwt
    ) {
        String correoDemandante = jwt.getSubject();
        IntercambioDTO dto = intercambioService.solicitarIntercambio(id, correoDemandante);
        IntercambioUsuarioDTO iu = intercambioUsuarioService.obtenerPorIntercambioUsuarioEstado(id, correoDemandante,EstadoIntercambio.EMPAREJAMIENTO);
            Usuario destinatario = usuarioService.obtenerPorCorreo(iu.getCreadorCorreo());
            String mensaje = "El usuario " + iu.getCreadorNombre() + " ha solicitado el intercambio: " 
                + iu.getIntercambioNombre();
            notificacionService.crearYEnviar(destinatario, TipoNotificacion.INTERCAMBIO, mensaje, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}/acuerdo")
    public ResponseEntity<IntercambioUsuarioDTO> establecerAcuerdo(
            @PathVariable Integer id,
            @RequestBody AcuerdoRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String correo = jwt.getSubject();
        IntercambioUsuarioDTO dto = intercambioUsuarioService.establecerAcuerdo(id, request, correo);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/solicitudes")
    public ResponseEntity<List<IntercambioUsuarioDTO>> obtenerSolicitudesPendientes(
        @AuthenticationPrincipal Jwt jwt) {
        String correo = jwt.getSubject();
        List<IntercambioUsuarioDTO> solicitudes = intercambioUsuarioService.obtenerSolicitudesPendientes(correo);
        return ResponseEntity.ok(solicitudes);
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarAcuerdo(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {
        try{
            IntercambioUsuarioDTO iu = intercambioUsuarioService.obtenerPorId(id);
            Usuario destinatario = usuarioService.obtenerPorCorreo(iu.getSolicitanteCorreo());
            String mensaje = "El usuario " + iu.getCreadorNombre() + " ha finalizado el intercambio: " 
                + iu.getIntercambioNombre();
            notificacionService.crearYEnviar(destinatario, TipoNotificacion.INTERCAMBIO, mensaje, null);
            String correo = jwt.getSubject();
            IntercambioUsuarioDTO dto = intercambioUsuarioService.finalizarAcuerdo(id, correo);
            return ResponseEntity.ok(dto);
        }catch(ResponseStatusException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }  
    }
    @PutMapping("/solicitudes/{id}/aceptar")
    public ResponseEntity<?> aceptarSolicitud(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {
        try{
            String correo = jwt.getSubject();
            IntercambioUsuarioDTO iu = intercambioUsuarioService.obtenerPorId(id);
            Usuario destinatario = usuarioService.obtenerPorCorreo(iu.getSolicitanteCorreo());
            String mensaje = "El usuario " + iu.getCreadorNombre() + " ha aceptado tu solicitud de intercambio: " 
            + iu.getIntercambioNombre();
            notificacionService.crearYEnviar(destinatario, TipoNotificacion.INTERCAMBIO, mensaje, null);
            IntercambioDTO dto = intercambioUsuarioService.aceptarSolicitud(id, correo);
            return ResponseEntity.ok(dto);
        }catch(ResponseStatusException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        
    }

    @PutMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {
        try{
            String correo = jwt.getSubject();
            IntercambioUsuarioDTO iu = intercambioUsuarioService.obtenerPorId(id);
            Usuario destinatario = usuarioService.obtenerPorCorreo(iu.getSolicitanteCorreo());
            String mensaje = "El usuario " + iu.getCreadorNombre() + " ha rechazado tu solicitud de intercambio: " 
            + iu.getIntercambioNombre();
            notificacionService.crearYEnviar(destinatario, TipoNotificacion.INTERCAMBIO, mensaje, null);

            intercambioUsuarioService.rechazarSolicitud(id, correo);
            return ResponseEntity.noContent().build();
        }catch(ResponseStatusException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/historial")
    public ResponseEntity<List<Intercambio>> obtenerHistorial(@AuthenticationPrincipal Jwt jwt) {
        String correo = jwt.getSubject();
        Usuario user = usuarioService.obtenerPorCorreo(correo);
        return ResponseEntity.ok(intercambioService.obtenerHistorial(user));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<Intercambio>> filtrar(
            @RequestParam(required = false) TipoIntercambio tipo,
            @RequestParam(required = false) ModalidadServicio modalidad,
            @RequestParam(required = false) List<Long> categorias,
            @RequestParam(required = false) Double minHoras,
            @RequestParam(required = false) Double maxHoras,
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(
                intercambioService.filtrar(tipo, modalidad, categorias, minHoras, maxHoras, q));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIntercambio(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {

        String correo = jwt.getSubject();
        Usuario user = usuarioService.obtenerPorCorreo(correo);

        IntercambioDTO intercambio = intercambioService.obtenerPorId(id);

        // Permitir eliminar si es el dueño o si es admin
        boolean esAdmin = user.getRoles().contains(Role.ADMIN);

        if (!intercambio.getCorreoOfertante().equals(correo) && !esAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        intercambioService.eliminarIntercambio(id);
        return ResponseEntity.noContent().build();
    }
}
