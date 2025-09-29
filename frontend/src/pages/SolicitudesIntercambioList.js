import { useEffect, useState } from "react";
import { useAuth } from "../utils/AuthContext";
import { useNavigate } from "react-router-dom";
import {
  obtenerSolicitudes,
  aceptarSolicitud,
  rechazarSolicitud,
} from "../services/intercambioService";

export default function SolicitudesIntercambio() {
  const { token } = useAuth();
  const [solicitudes, setSolicitudes] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    if (token) {
      obtenerSolicitudes(token).then(setSolicitudes).catch(console.error);
    }
  }, [token]);

  const handleAceptar = async (solicitudId) => {
    try {
      console.log(solicitudId);
      const respuesta = await aceptarSolicitud(solicitudId, token);

      // Actualizamos la lista eliminando la solicitud aceptada
      setSolicitudes((prev) =>
        prev.filter((s) => s.id !== solicitudId)
      );

      // Redirigimos al chat asociado a la conversación
      if (respuesta.conversacionId) {
        navigate(`/chat/${respuesta.conversacionId}`);
      } else {
        console.warn("No se encontró el ID de la conversación.");
      }
    } catch (error) {
      console.error(error);
    }
  };

  const handleRechazar = async (id) => {
    await rechazarSolicitud(id, token);
    setSolicitudes((prev) => prev.filter((s) => s.id !== id));
  };

  return (
    <div style={{ maxWidth: "800px", margin: "40px auto" }}>
      <h1>Solicitudes de Intercambio</h1>
      {solicitudes.length === 0 ? (
        <p>No tienes solicitudes pendientes.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {solicitudes.map((s) => (
            <li
              key={s.id}
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                background: "#f9fafb",
                padding: "12px",
                borderRadius: "8px",
                marginBottom: "10px",
              }}
            >
              <span>
                <strong>{s.usuarioNombre}</strong> quiere unirse a{" "}
                <em>{s.intercambioNombre}</em>
              </span>
              <div style={{ display: "flex", gap: "8px" }}>
                <button
                  onClick={() => handleAceptar(s.id)}
                  style={{
                    background: "#22c55e",
                    color: "white",
                    border: "none",
                    padding: "6px 12px",
                    borderRadius: "6px",
                    cursor: "pointer",
                  }}
                >
                  Aceptar
                </button>
                <button
                  onClick={() => handleRechazar(s.id)}
                  style={{
                    background: "#ef4444",
                    color: "white",
                    border: "none",
                    padding: "6px 12px",
                    borderRadius: "6px",
                    cursor: "pointer",
                  }}
                >
                  Rechazar
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
