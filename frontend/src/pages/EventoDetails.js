import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import {
  getEventoById,
  registrarParticipacion,
  getParticipantesEventoById,
} from "../services/eventoService";
import { useAuth } from "../utils/AuthContext";

const EventoDetalle = () => {
  const { id } = useParams();
  const [evento, setEvento] = useState(null);
  const [participaciones, setParticipaciones] = useState([]);
  const { user } = useAuth();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const eventoData = await getEventoById(id);
        setEvento(eventoData);

        const participacionesData = await getParticipantesEventoById(id);
        setParticipaciones(participacionesData);
      } catch (err) {
        console.error("Error cargando datos:", err);
      }
    };
    fetchData();
  }, [id]);

  const handleParticipar = async () => {
    try {
      const nueva = await registrarParticipacion(id, user.correo);
      setParticipaciones((prev) => [...prev, nueva]);
      alert("Te has unido al evento con éxito");
    } catch (err) {
      const mensaje = err.response?.data?.message || "Error al unirse al evento";
      alert(mensaje);
    }
  };

  if (!evento) return <p>Cargando evento...</p>;

  return (
    <div style={styles.container}>
      {console.log(evento)}
      <div style={styles.card}>
        <h1 style={styles.nombre}>{evento.nombre}</h1>

        {/* Estado del evento */}
        <span
          style={{
            ...styles.estado,
            ...(estadoStyles[evento.estadoEvento] || {}),
          }}
        >
          {evento.estadoEvento}
        </span>

        <p style={styles.fecha}>
          📅 {new Date(evento.fechaEvento).toLocaleString()}
        </p>

        {/* Ubicación */}
        <p style={styles.ubicacion}>📍 {evento.ubicacion}</p>

        <p style={styles.descripcion}>{evento.descripcion}</p>

        <div style={styles.organizador}>
          <img
            src={evento.organizador.fotoPerfil || "/default-user.png"}
            alt="organizador"
            style={styles.organizadorImg}
          />
          <Link to={`/perfil/${evento.organizador.correo}`}>
            {evento.organizador.nombre}
          </Link>
        </div>

        <button
          onClick={handleParticipar}
          style={styles.btn}
          onMouseOver={(e) => (e.target.style.background = "#e65c00")}
          onMouseOut={(e) => (e.target.style.background = "#ff6600")}
          disabled={evento.estadoEvento === "FINALIZADO"}
        >
          {evento.estadoEvento === "FINALIZADO"
            ? "Evento finalizado"
            : "Unirme al evento"}
        </button>

        <div style={styles.participantes}>
          <h3>Participantes</h3>
          <div style={styles.participantesList}>
            {participaciones.length > 0 ? (
              participaciones.map((p) => (
                <div key={p.id} style={styles.participanteCard}>
                  <img
                    src={p.fotoPerfil || "/default-user.png"}
                    alt={p.nombre || "Usuario"}
                    style={styles.participanteImg}
                  />
                  <Link to={`/perfil/${p.correo}`}>{p.nombre}</Link>
                  {p.asistio && (
                    <span style={{ marginLeft: "8px", color: "green" }}>
                      ✔ Asistió
                    </span>
                  )}
                </div>
              ))
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
  container: {
    display: "flex",
    justifyContent: "center",
    padding: "2rem",
    background: "#f9fafb",
    minHeight: "100vh",
  },
  card: {
    maxWidth: "700px",
    width: "100%",
    background: "#fff",
    borderRadius: "16px",
    boxShadow: "0 4px 10px rgba(0,0,0,0.08)",
    padding: "2rem",
  },
  nombre: {
    fontSize: "2rem",
    fontWeight: "bold",
    marginBottom: "0.5rem",
    color: "#333",
  },
  estado: {
    display: "inline-block",
    padding: "0.3rem 0.8rem",
    borderRadius: "12px",
    fontWeight: "bold",
    marginBottom: "1rem",
  },
  fecha: { fontSize: "0.9rem", color: "#666", marginBottom: "0.5rem" },
  ubicacion: { fontSize: "1rem", color: "#444", marginBottom: "1rem" },
  descripcion: { fontSize: "1rem", marginBottom: "1.5rem", color: "#444" },
  organizador: {
    display: "flex",
    alignItems: "center",
    gap: "0.8rem",
    marginBottom: "2rem",
  },
  organizadorImg: {
    width: "45px",
    height: "45px",
    borderRadius: "50%",
    objectFit: "cover",
  },
  btn: {
    background: "#ff6600",
    color: "white",
    padding: "0.7rem 1.5rem",
    border: "none",
    borderRadius: "12px",
    fontWeight: "bold",
    cursor: "pointer",
    transition: "background 0.3s",
    marginBottom: "2rem",
  },
  participantes: { borderTop: "1px solid #eee", paddingTop: "1rem" },
  participantesList: { display: "flex", flexWrap: "wrap", gap: "1rem" },
  participanteCard: {
    display: "flex",
    alignItems: "center",
    gap: "0.5rem",
    background: "#f3f4f6",
    padding: "0.5rem 1rem",
    borderRadius: "12px",
  },
  participanteImg: {
    width: "35px",
    height: "35px",
    borderRadius: "50%",
    objectFit: "cover",
  },
};

const estadoStyles = {
    DISPONIBLE: { backgroundColor: "#4CAF50", color: "white" }, 
    FINALIZADO: { backgroundColor: "#f44336", color: "white" },
  };

export default EventoDetalle;
