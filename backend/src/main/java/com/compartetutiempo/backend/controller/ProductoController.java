package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.model.Producto;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoProducto;
import com.compartetutiempo.backend.model.enums.Role;
import com.compartetutiempo.backend.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.compartetutiempo.backend.service.UsuarioService;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    private final UsuarioService usuarioService;

    public ProductoController(ProductoService productoService, UsuarioService usuarioService) {
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto,
            @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject(); // aquí está el correo/username del token
        Usuario user = usuarioService.obtenerPorCorreo(email);

        producto.setUser(user);
        producto.setFechaPublicacion(new Date());
        producto.setEstado(EstadoProducto.DISPONIBLE);

        return ResponseEntity.ok(productoService.crear(producto));
    }

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<Producto>> obtenerPorUsuario(@AuthenticationPrincipal Jwt jwt) {
        String correo = jwt.getSubject(); // sacamos el correo del token
        Usuario user = usuarioService.obtenerPorCorreo(correo);
        List<Producto> productos = productoService.obtenerPorUsuario(user);
        return ResponseEntity.ok(productos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto productoModificado,
            @AuthenticationPrincipal Jwt jwt) {

        String correo = jwt.getSubject();
        Usuario user = usuarioService.obtenerPorCorreo(correo);

        Producto actualizado = productoService.actualizarProducto(id, productoModificado, user);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        String correo = jwt.getSubject();
        Usuario user = usuarioService.obtenerPorCorreo(correo);

        // Si es ADMIN, eliminamos sin validar propietario
        if (user.getRoles().contains(Role.ADMIN)) {
            productoService.eliminarProductoComoAdmin(id);
        } else {
            productoService.eliminarProducto(id, user);
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/historial")
    public ResponseEntity<List<Producto>> obtenerHistorial(@AuthenticationPrincipal Jwt jwt) {
        String correo = jwt.getSubject();
        Usuario user = usuarioService.obtenerPorCorreo(correo);
        return ResponseEntity.ok(productoService.obtenerHistorial(user));
    }
}
