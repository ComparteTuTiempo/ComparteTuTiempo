import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import {
  getEventoById,
  registrarParticipacion,
  getParticipantesEventoById,
} from "../services/eventoService";
import { useAuth } from "../utils/AuthContext";

const EventoDetalle = ({ id: propId }) => {
  const { id: routeId } = useParams();
  const id = propId || routeId; 

  const navigate = useNavigate();
  const [evento, setEvento] = useState(null);
  const [participaciones, setParticipaciones] = useState([]);
  const { user, token } = useAuth();

  useEffect(() => {
    if (!id) return;
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
      const nueva = await registrarParticipacion(id, user.correo, token);
      setParticipaciones((prev) => [...prev, nueva]);
      alert("Te has unido al evento con éxito");
    } catch (err) {
      const mensaje = err.response?.data?.message || "Error al unirse al evento";
      alert(mensaje);
    }
  };

  if (!evento) return <p>Cargando evento...</p>;

  const esOrganizador = user?.correo === evento.organizador.correo;
  const eventoFinalizado = evento.estadoEvento === "FINALIZADO";

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h1 style={styles.nombre}>{evento.nombre}</h1>

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

        {/* Botones de acción */}
        <div style={styles.acciones}>
          {!esOrganizador && (
            <button
              onClick={handleParticipar}
              style={{ ...styles.btn, background: "#ff6600" }}
              disabled={eventoFinalizado}
            >
              {eventoFinalizado ? "Evento finalizado" : "Unirme al evento"}
            </button>
          )}

          {esOrganizador && (
            <button
              onClick={() => navigate(`/eventos/${evento.id}/participantes/lista`)}
              style={{ ...styles.btn, background: "#007bff" }}
              disabled={eventoFinalizado}
            >
              {eventoFinalizado ? "Evento finalizado" : "Gestionar asistencia"}
            </button>
          )}
        </div>

        {/* Participantes */}
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
  acciones: {
    display: "flex",
    gap: "1rem",
    marginBottom: "2rem",
  },
  btn: {
    color: "white",
    padding: "0.7rem 1.5rem",
    border: "none",
    borderRadius: "12px",
    fontWeight: "bold",
    cursor: "pointer",
    transition: "background 0.3s",
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
