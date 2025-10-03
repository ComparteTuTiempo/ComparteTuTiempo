import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useAuth } from "../utils/AuthContext";
import { obtenerDetalleIntercambioUsuario } from "../services/intercambioService";

export default function DetalleIntercambioUsuario({ id: propId }) {
  const { id: paramId } = useParams();
  const id = propId || paramId; 
  const { user, token } = useAuth();
  const [intercambio, setIntercambio] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;

    setLoading(true);
    obtenerDetalleIntercambioUsuario(id, token)
      .then(setIntercambio)
      .catch((err) => {
        console.error("Error al cargar intercambio:", err);
        alert("❌ No se pudo cargar el intercambio");
      })
      .finally(() => setLoading(false));
  }, [id, token]);

  if (!intercambio)
    return <p style={{ textAlign: "center" }}>Cargando...</p>;

  const esSolicitante = intercambio.solicitanteCorreo === user.correo;
  const esCreador = intercambio.creadorCorreo === user.correo;

  const getEstadoStyle = (estado) => {
    switch (estado) {
      case "EMPAREJAMIENTO": return { backgroundColor: "#ffc107", color: "black" };
      case "CONSENSO": return { backgroundColor: "#007bff", color: "white" };
      case "EJECUCION": return { backgroundColor: "#28a745", color: "white" };
      case "FINALIZADO": return { backgroundColor: "#6c757d", color: "white" };
      default: return { backgroundColor: "#6c757d", color: "white" };
    }
  };

  return (
    <div style={{ display: "flex", justifyContent: "center", marginTop: "40px", padding: "20px" }}>
      <div style={{
        maxWidth: "600px",
        width: "100%",
        background: "#fff",
        borderRadius: "10px",
        padding: "30px",
        boxShadow: "0 4px 10px rgba(0,0,0,0.1)"
      }}>
        <h1 style={{ marginBottom: "10px" }}>{intercambio.intercambioNombre}</h1>

        {/* Badge de estado */}
        <span style={{
          display: "inline-block",
          padding: "6px 12px",
          borderRadius: "20px",
          fontSize: "14px",
          fontWeight: "bold",
          ...getEstadoStyle(intercambio.estado)
        }}>
          {intercambio.estado}
        </span>

        <p style={{ marginTop: "20px", fontSize: "16px" }}>{intercambio.terminos}</p>
        <p><strong>Horas asignadas:</strong> {intercambio.horasAsignadas}</p>
        <p><strong>Solicitante:</strong> {intercambio.solicitanteNombre} ({intercambio.solicitanteCorreo})</p>
        <p><strong>Ofertante:</strong> {intercambio.creadorNombre} ({intercambio.creadorCorreo})</p>

        {intercambio.conversacionId && (
          <button
            style={{
              marginTop: "15px",
              padding: "10px 20px",
              backgroundColor: "#28a745",
              color: "white",
              border: "none",
              borderRadius: "8px",
              cursor: "pointer"
            }}
            onClick={() => window.location.href = `/conversaciones/${intercambio.conversacionId}`}
          >
            💬 Ir al chat
          </button>
        )}
      </div>
    </div>
  );
}

