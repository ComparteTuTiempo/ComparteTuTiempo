import React, { useEffect, useState } from "react";
import { useAuth } from "../utils/AuthContext";
import { obtenerMisTransacciones, finalizarTransaccion } from "../services/productoUsuarioService";
import { useNavigate } from "react-router-dom";

export default function MisTransaccionesProducto() {
  const { token, user } = useAuth();
  const [transacciones, setTransacciones] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    if (!token) return;
    const fetchData = async () => {
      try {
        const data = await obtenerMisTransacciones(token);
        setTransacciones(data);
      } catch (err) {
        console.error("Error cargando transacciones:", err);
      }
    };
    fetchData();
  }, [token]);

  const handleFinalizar = async (id) => {
    try {
      await finalizarTransaccion(id, token);
      setTransacciones((prev) =>
        prev.map((t) =>
          t.id === id ? { ...t, estado: "FINALIZADA" } : t
        )
      );
      alert("✅ Transacción finalizada");
    } catch (err) {
      alert("Error al finalizar: " + err.response?.data?.message);
    }
  };

  const getBadgeStyle = (estado) => {
    switch (estado) {
      case "EN_PROCESO":
        return { backgroundColor: "#ffc107", color: "black" };
      case "FINALIZADA":
        return { backgroundColor: "#dc3545", color: "white" };
      default:
        return { backgroundColor: "#d0ca1bff", color: "white" };
    }
  };

  return (
    <div style={{ maxWidth: "900px", margin: "40px auto", padding: "20px" }}>
      <h1>Mis Transacciones</h1>

      {transacciones.length === 0 ? (
        <p style={{ textAlign: "center" }}>No tienes transacciones todavía.</p>
      ) : (
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))",
            gap: "20px",
          }}
        >
          {transacciones.map((t) => (
            <div
              key={t.id}
              style={{
                border: "1px solid #ddd",
                borderRadius: "10px",
                padding: "20px",
                boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
              }}
            >
              <h3>{t.productoNombre}</h3>
              <span
                style={{
                  display: "inline-block",
                  padding: "4px 10px",
                  borderRadius: "15px",
                  fontSize: "13px",
                  fontWeight: "bold",
                  ...getBadgeStyle(t.estado),
                }}
              >
                {t.estado}
              </span>

              <p style={{ marginTop: "10px" }}>
                <strong>Propietario:</strong> {t.propietarioNombre}
              </p>
              <p>
                <strong>Comprador:</strong> {t.compradorNombre}
              </p>
              <p>
                <strong>Horas:</strong> {t.productoHoras}
              </p>

              {/* Botón de chat */}
              <button
                onClick={() => navigate(`/conversaciones/${t.id}`)}
                style={{
                  marginTop: "10px",
                  padding: "8px 12px",
                  backgroundColor: "#007bff",
                  color: "white",
                  border: "none",
                  borderRadius: "6px",
                  cursor: "pointer",
                  width: "100%",
                }}
              >
                💬 Ir al chat
              </button>

              {t.estado === "PENDIENTE" &&
                t.propietarioCorreo === user.correo && (
                  <button
                    onClick={() => handleFinalizar(t.id)}
                    style={{
                      marginTop: "10px",
                      padding: "8px 12px",
                      backgroundColor: "#dc3545",
                      color: "white",
                      border: "none",
                      borderRadius: "6px",
                      cursor: "pointer",
                      width: "100%",
                    }}
                  >
                    🔒 Finalizar
                  </button>
                )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
