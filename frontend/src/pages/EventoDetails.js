import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { getEventoById, registrarParticipacion } from "../services/eventoService";
import { useAuth } from "../utils/AuthContext";

const EventoDetalle = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const [evento, setEvento] = useState(null);

  useEffect(() => {
    const fetchEvento = async () => {
      try {
        const data = await getEventoById(id);
        setEvento(data);
      } catch (err) {
        console.error("Error cargando evento:", err);
      }
    };
    fetchEvento();
  }, [id]);

  const handleParticipar = async () => {
    try {
      const eventoActualizado = await registrarParticipacion(id, user.correo);
      setEvento(eventoActualizado);

      alert("Te has unido al evento con éxito");
    } catch (error) {
      const mensaje = error.response?.data?.message || "Error al unirse al evento";
      alert(mensaje);
    }
  };

  if (!evento) return <p>Cargando evento...</p>;

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h1 style={styles.nombre}>{evento.nombre}</h1>
        <p style={styles.fecha}>📅 {new Date(evento.fechaEvento).toLocaleString()}</p>
        <p style={styles.descripcion}>{evento.descripcion}</p>

        <div style={styles.organizador}>
          <img
            src={evento.organizador.fotoPerfil || "/img/usuario-standar.png"}
            alt={evento.organizador.nombre || "Organizador"}
            style={styles.organizadorImg}
          />
          <Link to={`/perfil/${evento.organizador.correo}`}>
            {evento.organizador.nombre || "Organizador"}
          </Link>
        </div>

        <button
          onClick={handleParticipar}
          style={styles.btn}
          onMouseOver={e => (e.target.style.background = "#e65c00")}
          onMouseOut={e => (e.target.style.background = "#ff6600")}
        >
          Unirme al evento
        </button>

        <div style={styles.participantes}>
          <h3>Participantes</h3>
          <div style={styles.participantesList}>
            {evento.participantes && evento.participantes.length > 0 ? (
              evento.participantes.map(p =>
                p ? (
                  <div key={p.correo} style={styles.participanteCard}>
                    <img
                      src={p.fotoPerfil || "/img/usuario-standar.png"}
                      alt={p.nombre || "Usuario"}
                      style={styles.participanteImg}
                    />
                    <Link to={`/perfil/${p.correo}`}>{p.nombre || "Usuario"}</Link>
                  </div>
                ) : null
              )
            ) : (
              <p>No hay participantes todavía</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

const styles = {
  container: { display: "flex", justifyContent: "center", padding: "2rem", background: "#f9fafb", minHeight: "100vh" },
  card: { maxWidth: "700px", width: "100%", background: "#fff", borderRadius: "16px", boxShadow: "0 4px 10px rgba(0,0,0,0.08)", padding: "2rem" },
  nombre: { fontSize: "2rem", fontWeight: "bold", marginBottom: "0.5rem", color: "#333" },
  fecha: { fontSize: "0.9rem", color: "#666", marginBottom: "1rem" },
  descripcion: { fontSize: "1rem", marginBottom: "1.5rem", color: "#444" },
  organizador: { display: "flex", alignItems: "center", gap: "0.8rem", marginBottom: "2rem" },
  organizadorImg: { width: "45px", height: "45px", borderRadius: "50%", objectFit: "cover" },
  btn: { background: "#ff6600", color: "white", padding: "0.7rem 1.5rem", border: "none", borderRadius: "12px", fontWeight: "bold", cursor: "pointer", transition: "background 0.3s", marginBottom: "2rem" },
  participantes: { borderTop: "1px solid #eee", paddingTop: "1rem" },
  participantesList: { display: "flex", flexWrap: "wrap", gap: "1rem" },
  participanteCard: { display: "flex", alignItems: "center", gap: "0.5rem", background: "#f3f4f6", padding: "0.5rem 1rem", borderRadius: "12px" },
  participanteImg: { width: "35px", height: "35px", borderRadius: "50%", objectFit: "cover" },
};

export default EventoDetalle;
