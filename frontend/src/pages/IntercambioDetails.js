import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useAuth } from "../utils/AuthContext";
import {
  obtenerIntercambioPorId,
  solicitarIntercambio
} from "../services/intercambioService";
import axios from "axios";

export default function IntercambioDetalle() {
  const { id } = useParams();
  const { token, user } = useAuth();
  const [intercambio, setIntercambio] = useState(null);
  const [loading, setLoading] = useState(true);
  const [esParticipante, setEsParticipante] = useState(false);

  // reseñas
  const [resenas, setResenas] = useState([]);
  const [promedio, setPromedio] = useState(0);
  const [nuevaResena, setNuevaResena] = useState({ puntuacion: 5, comentario: "" });

  useEffect(() => {
    if (token) {
      obtenerIntercambioPorId(id, token)
        .then((data) => {
          setIntercambio(data);
          const usuarioEsParticipante = data.participantes?.some(
            (p) => p.usuarioId === user.id
          );
          setEsParticipante(usuarioEsParticipante);

          // cargar reseñas
          axios.get(`http://localhost:8080/resenas/intercambios/${id}`)
            .then(res => setResenas(res.data))
            .catch(err => console.error("❌ Error al cargar reseñas:", err));

          axios.get(`http://localhost:8080/resenas/intercambios/${id}/promedio`)
            .then(res => setPromedio(res.data))
            .catch(err => console.error("❌ Error al cargar promedio:", err));

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
      alert("Error al enviar la solicitud: " + err.response?.data.message);
    }
  };

  const enviarResena = async () => {
    if (!nuevaResena.comentario.trim()) {
      alert("⚠ El comentario no puede estar vacío");
      return;
    }
    try {
      await axios.post(
        `http://localhost:8080/resenas/intercambios/${id}`,
        {
          ...nuevaResena,
          intercambio: { id: parseInt(id, 10) } // 👈 añadimos el intercambio
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // refrescar reseñas y promedio
      const res = await axios.get(`http://localhost:8080/resenas/intercambios/${id}`);
      setResenas(res.data);
      const avg = await axios.get(`http://localhost:8080/resenas/intercambios/${id}/promedio`);
      setPromedio(avg.data);

      setNuevaResena({ puntuacion: 5, comentario: "" });
    } catch (err) {
      console.error("❌ Error al enviar reseña:", err);
      alert("No se pudo enviar la reseña");
    }
  };

  if (loading) {
    return <p style={{ textAlign: "center", marginTop: "20px" }}>Cargando intercambio...</p>;
  }

  if (!intercambio) {
    return (
      <p style={{ textAlign: "center", marginTop: "20px", color: "red" }}>
        No se encontró el intercambio.
      </p>
    );
  }

  // Verificar si el usuario es el creador del intercambio
  const esCreador = user?.correo === intercambio.usuarioCorreo;

  // Verificar si ya reseñó
  const yaHaResenado = user && resenas.some(r => r.autor?.correo === user.correo);

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
        <span>{intercambio.usuarioNombre || intercambio.usuarioCorreo}</span>
      </div>

      {/* Botón de solicitud */}
      {!esCreador && intercambio.estado !== "FINALIZADO" && (
        <div>
          <button onClick={handleSolicitar} style={styles.solicitarBtn}>
            Solicitar intercambio
          </button>
        </div>
      )}

      {intercambio.estado === "FINALIZADO" && (
        <p style={styles.finalizado}>✅ Este intercambio ha finalizado.</p>
      )}

      {/* Reseñas */}
      {intercambio.tipo === "OFERTA" && (
        <div style={styles.reviews}>
          <h3>⭐ Promedio reseñas: {promedio.toFixed(1)} / 5</h3>

          {resenas.length > 0 ? (
            resenas.map((r) => (
              <div key={r.id} style={styles.review}>
                <strong>{r.autor?.nombre || "Anónimo"}</strong> ⭐ {r.puntuacion}
                <p>{r.comentario}</p>
              </div>
            ))
          ) : (
            <p>No hay reseñas aún.</p>
          )}

          {!esCreador && !yaHaResenado && (
            <div style={styles.newReview}>
              <h4>Dejar una reseña</h4>
              <select
                value={nuevaResena.puntuacion}
                onChange={(e) =>
                  setNuevaResena({ ...nuevaResena, puntuacion: parseInt(e.target.value) })
                }
                style={{ marginRight: "10px", padding: "5px" }}
              >
                {[1, 2, 3, 4, 5].map((n) => (
                  <option key={n} value={n}>{n}</option>
                ))}
              </select>
              <textarea
                placeholder="Escribe tu comentario..."
                value={nuevaResena.comentario}
                onChange={(e) => setNuevaResena({ ...nuevaResena, comentario: e.target.value })}
                style={{ width: "100%", minHeight: "60px", marginTop: "10px", padding: "5px" }}
              />
              <button
                onClick={enviarResena}
                style={{
                  marginTop: "10px",
                  backgroundColor: "#28a745",
                  color: "white",
                  border: "none",
                  padding: "8px 12px",
                  borderRadius: "6px",
                  cursor: "pointer"
                }}
              >
                Enviar reseña
              </button>
            </div>
          )}
        </div>
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
  reviews: { marginTop: "20px", padding: "15px", borderTop: "1px solid #ddd" },
  review: { borderBottom: "1px solid #eee", padding: "6px 0" },
  newReview: { marginTop: "15px" },
};
