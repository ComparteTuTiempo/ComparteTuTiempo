package com.compartetutiempo.backend.controller;

import com.compartetutiempo.backend.dto.ProductoUsuarioDTO;
import com.compartetutiempo.backend.model.Conversacion;
import com.compartetutiempo.backend.model.Producto;
import com.compartetutiempo.backend.model.ProductoUsuario;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoProductoUsuario;
import com.compartetutiempo.backend.service.NotificacionService;
import com.compartetutiempo.backend.service.ProductoService;
import com.compartetutiempo.backend.service.ProductoUsuarioService;
import com.compartetutiempo.backend.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductoUsuarioController.class)
@Import(ProductoUsuarioControllerTests.MockConfig.class)
class ProductoUsuarioControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoUsuarioService productoUsuarioService;

    @Autowired
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Producto producto;
    private ProductoUsuario transaccion;

    private final String correoUsuario = "test@mail.com";

    @TestConfiguration
    static class MockConfig {
        @Bean
        ProductoUsuarioService productoUsuarioService() { return Mockito.mock(ProductoUsuarioService.class); }
        @Bean
        UsuarioService usuarioService() { return Mockito.mock(UsuarioService.class); }
        @Bean
        NotificacionService notificacionService() { return Mockito.mock(NotificacionService.class); }
        @Bean
        ProductoService productoService() { return Mockito.mock(ProductoService.class); }
    }

    private final org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser =
            jwt().jwt(jwt -> jwt.subject(correoUsuario));

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo(correoUsuario);
        usuario.setNombre("Usuario Test");

        producto = new Producto();
        producto.setId(1);
        producto.setNombre("Producto Test");
        producto.setNumeroHoras(5);
        producto.setPropietario(usuario);

        Conversacion conversacion = new Conversacion();
        conversacion.setId(99);

        transaccion = new ProductoUsuario();
        transaccion.setId(100);
        transaccion.setProducto(producto);
        transaccion.setComprador(usuario);
        transaccion.setEstado(EstadoProductoUsuario.PENDIENTE);
        transaccion.setConversacion(conversacion);

    }

    @Test
    @DisplayName("POST /productousuario/adquirir/{id} debe cambiar estado a PENDIENTE")
    void adquirirProducto_OK() throws Exception {
        when(usuarioService.obtenerPorCorreo(correoUsuario)).thenReturn(usuario);
        when(productoUsuarioService.adquirirProducto(eq(1L), eq(usuario))).thenReturn(transaccion);

        mockMvc.perform(post("/productousuario/adquirir/1")
                        .with(jwtUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Producto Test"));

        // verificamos que el servicio fue llamado
        verify(productoUsuarioService).adquirirProducto(1L, usuario);
    }

    @Test
    @DisplayName("PUT /productousuario/finalizar/{id} debe cambiar estado a FINALIZADO")
    void finalizarTransaccion_OK() throws Exception {
        when(usuarioService.obtenerPorCorreo(correoUsuario)).thenReturn(usuario);
        when(productoUsuarioService.finalizarTransaccion(eq(100), eq(usuario))).thenReturn(transaccion);

        mockMvc.perform(put("/productousuario/finalizar/100")
                        .with(jwtUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.productoNombre").value("Producto Test"));

        verify(productoUsuarioService).finalizarTransaccion(100, usuario);
    }

    @Test
    @DisplayName("GET /productousuario/transacciones debe devolver lista de transacciones")
    void obtenerMisTransacciones_OK() throws Exception {
        ProductoUsuarioDTO dto = ProductoUsuarioDTO.fromEntity(transaccion);
        when(productoUsuarioService.obtenerMisTransacciones(correoUsuario)).thenReturn(List.of(dto));

        mockMvc.perform(get("/productousuario/transacciones")
                        .with(jwtUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
@DisplayName("DELETE /productousuario/transacciones/{id}/cancelar debe eliminar la solicitud")
void cancelarSolicitud_Elimina() throws Exception {

    ProductoUsuarioDTO dto = ProductoUsuarioDTO.fromEntity(transaccion);
    when(productoUsuarioService.obtenerMisTransacciones(correoUsuario)).thenReturn(List.of(dto));

    // Ejecutamos cancelar
    doAnswer(invocation -> {
       
        when(productoUsuarioService.obtenerMisTransacciones(correoUsuario)).thenReturn(List.of());
        return null;
    }).when(productoUsuarioService).cancelarSolicitud(100, correoUsuario);

    
    mockMvc.perform(delete("/productousuario/transacciones/100/cancelar").with(jwtUser))
            .andExpect(status().isNoContent());

    
    mockMvc.perform(get("/productousuario/transacciones").with(jwtUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));

    verify(productoUsuarioService).cancelarSolicitud(100, correoUsuario);
}

}

