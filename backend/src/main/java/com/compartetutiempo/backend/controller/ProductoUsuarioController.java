package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.dto.ProductoUsuarioDTO;
import com.compartetutiempo.backend.model.ProductoUsuario;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.service.ProductoService;
import com.compartetutiempo.backend.service.ProductoUsuarioService;
import com.compartetutiempo.backend.service.UsuarioService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productousuario")
public class ProductoUsuarioController {

    private final ProductoUsuarioService productoUsuarioService;
    private final UsuarioService usuarioService;

    public ProductoUsuarioController(ProductoUsuarioService productoUsuarioService, UsuarioService usuarioService,
    ProductoService productoService) {
        this.productoUsuarioService = productoUsuarioService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/adquirir/{productoId}")
    public ResponseEntity<ProductoUsuarioDTO> adquirirProducto(
            @PathVariable Long productoId,
            @AuthenticationPrincipal Jwt jwt) {

        Usuario comprador = usuarioService.obtenerPorCorreo(jwt.getSubject());
        ProductoUsuario transaccion = productoUsuarioService.adquirirProducto(productoId, comprador);
        return ResponseEntity.ok(ProductoUsuarioDTO.fromEntity(transaccion));
    }

    @PutMapping("/finalizar/{transaccionId}")
    public ResponseEntity<ProductoUsuarioDTO> finalizarTransaccion(
            @PathVariable Integer transaccionId,
            @AuthenticationPrincipal Jwt jwt) {

        Usuario propietario = usuarioService.obtenerPorCorreo(jwt.getSubject());
        ProductoUsuario transaccionProducto = productoUsuarioService.finalizarTransaccion(transaccionId, propietario);
        return ResponseEntity.ok(ProductoUsuarioDTO.fromEntity(transaccionProducto));
    }

    @GetMapping("/transacciones")
    public ResponseEntity<List<ProductoUsuarioDTO>> obtenerMisTransaccionesProducto(
            @AuthenticationPrincipal Jwt jwt) {

        String correo = jwt.getSubject();
        return ResponseEntity.ok(productoUsuarioService.obtenerMisTransacciones(correo));
    }

    @DeleteMapping("/transacciones/{id}/cancelar")
    public ResponseEntity<Void> cancelarSolicitud(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {
        String correo = jwt.getSubject();
        productoUsuarioService.cancelarSolicitud(id, correo);
        return ResponseEntity.noContent().build();
    }

}
