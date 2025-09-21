import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useAuth } from "../utils/AuthContext";
import {
  obtenerIntercambioPorId,
  avanzarIntercambio,
  solicitarIntercambio
} from "../services/intercambioService";

export default function IntercambioDetalle() {
  const { id } = useParams();
  const { token, user } = useAuth();
  const [intercambio, setIntercambio] = useState(null);
  const [loading, setLoading] = useState(true);
  const [esParticipante, setEsParticipante] = useState(false);

  useEffect(() => {
    if (token) {
      obtenerIntercambioPorId(id, token)
        .then((data) => {
          setIntercambio(data);
          const usuarioEsParticipante = data.participantes?.some(
            p => p.usuarioId === user.id
          );
          setEsParticipante(usuarioEsParticipante);
          setLoading(false);
        })
        .catch((err) => {
          console.error(err);
          setLoading(false);
        });
    }
  }, [id, token, user]);

  const handleSolicitar = async () => {
    try {
      const actualizado = await solicitarIntercambio(id, token);
      setIntercambio(actualizado);
      setEsParticipante(true); 
      alert("Solicitud enviada correctamente");
    } catch (err) {
      console.error(err);
      alert("Error al enviar la solicitud: " + err.message);
    }
  };

  const avanzar = async () => {
    try {
      const actualizado = await avanzarIntercambio(id, token);
      setIntercambio(actualizado);
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) {
    return <p style={{ textAlign: "center", marginTop: "20px" }}>Cargando intercambio...</p>;
  }

  if (!intercambio) {
    return <p style={{ textAlign: "center", marginTop: "20px", color: "red" }}>
      No se encontró el intercambio.
    </p>;
  }

  // Verificar si el usuario es el creador del intercambio
  const esCreador = user?.correo === intercambio.usuarioCorreo;

  return (
    <div style={styles.container}>
      <h1 style={styles.title}>{intercambio.nombre}</h1>
      <span
        style={{
          ...styles.estado.base,
          ...styles.estado[intercambio.estado.toLowerCase()],
        }}
      >
        {intercambio.estado}
      </span>

      <div style={styles.info}>
        <p>⏰ {intercambio.numeroHoras} horas</p>
        <p>📅 {new Date(intercambio.fechaPublicacion).toLocaleString()}</p>
        <p>
          <strong>Modalidad:</strong> {intercambio.modalidad}
        </p>
        <p>
          <strong>Descripción:</strong> {intercambio.descripcion}
        </p>
      </div>

      <div style={styles.owner}>
        <img
          src={intercambio.usuarioFoto || "/default-avatar.png"}
          alt="Foto de perfil"
          style={styles.avatar}
        />
        <span>{intercambio.usuarioNombre}</span>
      </div>

      {/* Mostrar botón de solicitud solo si:
          - No es el creador
          - No es ya participante
          - El intercambio no está finalizado
      */}
      {!esCreador && !esParticipante && intercambio.estado !== "FINALIZADO" && (
        <div>
          <button onClick={handleSolicitar} style={styles.solicitarBtn}>
            Solicitar intercambio
          </button>
        </div>
      )}

      {/* Mostrar botón de avanzar estado solo para participantes */}
      {esParticipante && intercambio.estado !== "FINALIZADO" && (
        <div>
          <button onClick={avanzar} style={styles.btn}>
            Avanzar al siguiente estado
          </button>
        </div>
      )}

      {intercambio.estado === "FINALIZADO" && (
        <p style={styles.finalizado}>✅ Este intercambio ha finalizado.</p>
      )}
    </div>
  );
}

const styles = {
  container: {
    maxWidth: "800px",
    margin: "40px auto",
    padding: "20px",
    background: "#fff",
    borderRadius: "12px",
    boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
    fontFamily: "Arial, sans-serif",
  },
  title: { fontSize: "24px", fontWeight: "bold", marginBottom: "10px" },
  estado: {
    base: {
      display: "inline-block",
      padding: "6px 12px",
      borderRadius: "16px",
      fontSize: "14px",
      fontWeight: "600",
      marginBottom: "20px",
    },
    emparejamiento: { background: "#3b82f6", color: "#fff" },
    consenso: { background: "#facc15", color: "#000" },
    ejecucion: { background: "#22c55e", color: "#fff" },
    finalizado: { background: "#ef4444", color: "#fff" },
  },
  info: { color: "#444", marginBottom: "20px", lineHeight: "1.5" },
  owner: { display: "flex", alignItems: "center", gap: "10px", marginBottom: "20px" },
  avatar: { width: "40px", height: "40px", borderRadius: "50%", objectFit: "cover" },
  btn: {
    display: "inline-block",
    padding: "10px 16px",
    background: "#4f46e5",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontWeight: "600",
    marginBottom: "20px",
  },
  solicitarBtn: {
    display: "inline-block",
    padding: "10px 16px",
    background: "#10b981",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontWeight: "600",
    marginBottom: "20px",
  },
  finalizado: { textAlign: "center", color: "#16a34a", fontWeight: "600" },
  participantsTitle: { fontSize: "18px", fontWeight: "600", marginBottom: "12px" },
  participant: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "8px 12px",
    background: "#f9fafb",
    borderRadius: "8px",
    marginBottom: "8px",
  },
  participantBadge: {
    base: {
      padding: "4px 10px",
      borderRadius: "12px",
      fontSize: "13px",
      fontWeight: "500",
    },
    emparejamiento: { background: "#dbeafe", color: "#1d4ed8" },
    consenso: { background: "#fef9c3", color: "#854d0e" },
    ejecucion: { background: "#dcfce7", color: "#166534" },
    finalizado: { background: "#e5e7eb", color: "#374151" },
  },
};