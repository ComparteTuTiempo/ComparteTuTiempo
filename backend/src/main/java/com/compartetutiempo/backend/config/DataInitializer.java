package com.compartetutiempo.backend.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.compartetutiempo.backend.model.Categoria;
import com.compartetutiempo.backend.model.Evento;
import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.Producto;
import com.compartetutiempo.backend.model.Resena;
import com.compartetutiempo.backend.model.ResenaIntercambio;
import com.compartetutiempo.backend.model.Usuario;
import com.compartetutiempo.backend.model.enums.EstadoEvento;
import com.compartetutiempo.backend.model.enums.EstadoIntercambio;
import com.compartetutiempo.backend.model.enums.EstadoProducto;
import com.compartetutiempo.backend.model.enums.ModalidadServicio;
import com.compartetutiempo.backend.model.enums.TipoIntercambio;
import com.compartetutiempo.backend.repository.CategoriaRepository;
import com.compartetutiempo.backend.repository.EventoRepository;
import com.compartetutiempo.backend.repository.IntercambioRepository;
import com.compartetutiempo.backend.repository.ProductoRepository;
import com.compartetutiempo.backend.repository.ResenaRepository;
import com.compartetutiempo.backend.repository.ReseñaIntercambioRepository;
import com.compartetutiempo.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData(
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository,
            EventoRepository eventoRepository,
            IntercambioRepository intercambioRepository,
            ProductoRepository productoRepository,
            ResenaRepository resenaRepository,
            ReseñaIntercambioRepository resenaIntercambioRepository
    ) {
        return args -> {

            Random random = new Random();

            // ====== USUARIOS ======
            if (usuarioRepository.count() == 0) {
                String[] nombres = {"Ana López", "Carlos Pérez", "Lucía Gómez", "Miguel Torres", "Sofía Ramírez"};
                String[] ubicaciones = {"Madrid", "Barcelona", "Valencia", "Sevilla", "Bilbao"};
                String[] bios = {
                        "Apasionada por el arte y la música 🎨🎶",
                        "Me encanta el deporte y la vida sana 🏃‍♂️",
                        "Programador de día, gamer de noche 🎮",
                        "Voluntaria en asociaciones culturales 🤝",
                        "Fan de los idiomas y los viajes ✈️"
                };

                List<Usuario> usuarios = new ArrayList<>();
                for (int i = 0; i < nombres.length; i++) {
                    Usuario u = new Usuario();
                    u.setNombre(nombres[i]);
                    u.setCorreo("user" + (i+1) + "@tiempocompartido.com");
                    u.setContrasena(passwordEncoder.encode("1234abcd"));
                    u.setFechaNacimiento(LocalDate.of(1985 + i, (i % 12) + 1, (i % 28) + 1));
                    u.setBiografia(bios[i]);
                    u.setFotoPerfil("perfil" + (i+1) + ".jpg");
                    u.setNumeroHoras(1.0 + random.nextDouble() * 50);
                    u.setUbicacion(ubicaciones[i]);
                    u.setActivo(true);
                    u.setMetodoAutenticacion("LOCAL");
                    usuarios.add(u);
                }
                usuarioRepository.saveAll(usuarios);
            }

            // ====== CATEGORIAS ======
            if (categoriaRepository.count() == 0) {
                List<Categoria> categorias = Arrays.asList(
                        new Categoria("Deportes"),
                        new Categoria("Música"),
                        new Categoria("Tecnología"),
                        new Categoria("Arte"),
                        new Categoria("Idiomas"),
                        new Categoria("Gastronomía"),
                        new Categoria("Jardinería")
                );
                categoriaRepository.saveAll(categorias);
            }

            // ====== EVENTOS ======
            if (eventoRepository.count() == 0) {
                List<Usuario> usuarios = usuarioRepository.findAll();
                List<String> titulos = Arrays.asList(
                        "Taller de Fotografía",
                        "Partido de Fútbol Solidario",
                        "Clase de Cocina Italiana",
                        "Concierto Local",
                        "Intercambio de Idiomas"
                );
                List<String> descripciones = Arrays.asList(
                        "Aprende a usar tu cámara como un profesional.",
                        "Partido amistoso para recaudar fondos.",
                        "Prepara auténticas pastas y pizzas.",
                        "Descubre talentos emergentes en tu ciudad.",
                        "Practica inglés y español en un ambiente relajado."
                );

                List<Evento> eventos = new ArrayList<>();
                for (int i = 0; i < titulos.size(); i++) {
                    Evento ev = new Evento();
                    ev.setNombre(titulos.get(i));
                    ev.setDescripcion(descripciones.get(i));
                    ev.setDuracion(1.5 + random.nextInt(3));
                    ev.setUbicacion("Centro cultural " + (i+1));
                    ev.setFechaEvento(LocalDateTime.now().plusDays(3 * (i+1)));
                    ev.setOrganizador(usuarios.get(i % usuarios.size()));
                    ev.setEstadoEvento(i % 2 == 0 ? EstadoEvento.DISPONIBLE : EstadoEvento.FINALIZADO);
                    eventos.add(ev);
                }
                eventoRepository.saveAll(eventos);
            }

            // ====== INTERCAMBIOS ======
            if (intercambioRepository.count() == 0) {
                List<Usuario> usuarios = usuarioRepository.findAll();
                List<Categoria> categorias = categoriaRepository.findAll();
                List<Intercambio> intercambios = new ArrayList<>();

                String[] titulos = {
                        "Clases de guitarra",
                        "Entrenamiento personal",
                        "Ayuda con informática",
                        "Jardinería en terrazas",
                        "Traducción de textos"
                };

                for (int i = 0; i < titulos.length; i++) {
                    Intercambio in = new Intercambio();
                    in.setNombre(titulos[i]);
                    in.setDescripcion("Servicio de " + titulos[i].toLowerCase() + " ofrecido con experiencia.");
                    in.setFechaPublicacion(new Date(System.currentTimeMillis() - random.nextInt(1000000000)));
                    in.setNumeroHoras(2.0 + random.nextDouble() * 5);
                    in.setEstado(i % 2 == 0 ? EstadoIntercambio.EMPAREJAMIENTO : EstadoIntercambio.FINALIZADO);
                    in.setTipo(i % 2 == 0 ? TipoIntercambio.OFERTA : TipoIntercambio.PETICION);
                    in.setModalidad(i % 2 == 0 ? ModalidadServicio.PRESENCIAL : ModalidadServicio.VIRTUAL);
                    in.setUser(usuarios.get(i % usuarios.size()));
                    in.getCategorias().add(categorias.get(random.nextInt(categorias.size())));
                    intercambios.add(in);
                }
                intercambioRepository.saveAll(intercambios);
            }

            // ====== PRODUCTOS ======
            if (productoRepository.count() == 0) {
                List<Usuario> usuarios = usuarioRepository.findAll();
                List<String> nombres = Arrays.asList(
                        "Bicicleta de montaña",
                        "Guitarra clásica",
                        "Ordenador portátil",
                        "Juego de herramientas",
                        "Mesa de estudio"
                );
                List<Producto> productos = new ArrayList<>();
                for (int i = 0; i < nombres.size(); i++) {
                    Producto p = new Producto();
                    p.setNombre(nombres.get(i));
                    p.setDescripcion("Producto en buen estado: " + nombres.get(i).toLowerCase());
                    p.setFechaPublicacion(new Date(System.currentTimeMillis() - random.nextInt(500000000)));
                    p.setNumeroHoras(5.0 + random.nextDouble() * 15);
                    p.setEstado(i % 2 == 0 ? EstadoProducto.DISPONIBLE : EstadoProducto.RESERVADO);
                    p.setPropietario(usuarios.get(i % usuarios.size()));
                    productos.add(p);
                }
                productoRepository.saveAll(productos);
            }

            // ====== RESEÑAS ======
            if (resenaRepository.count() == 0) {
                List<Usuario> usuarios = usuarioRepository.findAll();
                String[] comentarios = {
                        "Muy buena experiencia, repetiría sin dudarlo.",
                        "Atento, puntual y muy simpático.",
                        "El servicio no cumplió mis expectativas.",
                        "Excelente calidad y trato profesional.",
                        "Todo perfecto, gracias."
                };

                List<Resena> resenas = new ArrayList<>();
                for (int i = 0; i < comentarios.length; i++) {
                    Resena r = new Resena();
                    r.setAutor(usuarios.get(i % usuarios.size()));
                    r.setDestinatario(usuarios.get((i+1) % usuarios.size()));
                    r.setPuntuacion((i % 5) + 1);
                    r.setComentario(comentarios[i]);
                    r.setFecha(LocalDateTime.now().minusDays(random.nextInt(30)));
                    resenas.add(r);
                }
                resenaRepository.saveAll(resenas);
            }

            // ====== RESEÑAS INTERCAMBIO ======
            if (resenaIntercambioRepository.count() == 0) {
                List<Usuario> usuarios = usuarioRepository.findAll();
                List<Intercambio> intercambios = intercambioRepository.findAll();
                String[] comentarios = {
                        "El intercambio fue muy enriquecedor.",
                        "Hubo problemas de comunicación.",
                        "Gran disposición y compromiso.",
                        "No repetiría, mal organizado.",
                        "Todo fluyó de maravilla."
                };

                List<ResenaIntercambio> reseñas = new ArrayList<>();
                for (int i = 0; i < comentarios.length; i++) {
                    ResenaIntercambio ri = new ResenaIntercambio();
                    ri.setIntercambio(intercambios.get(i % intercambios.size()));
                    ri.setAutor(usuarios.get(i % usuarios.size()));
                    ri.setPuntuacion((i % 5) + 1);
                    ri.setComentario(comentarios[i]);
                    ri.setFecha(LocalDateTime.now().minusDays(random.nextInt(15)));
                    reseñas.add(ri);
                }
                resenaIntercambioRepository.saveAll(reseñas);
            }
        };
    }
}

