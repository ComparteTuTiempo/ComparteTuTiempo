import React, { useEffect, useState } from "react";
import { useAuth } from "../utils/AuthContext";
import { obtenerMisTransacciones, finalizarTransaccion, cancelarSolicitud } from "../services/productoUsuarioService";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/Sidebar";

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
        return { backgroundColor: "#28a745", color: "white" };
      case "PENDIENTE":
        return { backgroundColor: "#007bff", color: "white" };
      default:
        return { backgroundColor: "#6c757d", color: "white" };
    }
  };

  return (
    <div style={styles.layout}>
      <Sidebar />

      <main style={styles.main}>
        <h2 style={styles.title}>📦 Mis Transacciones de Productos</h2>

        {transacciones.length === 0 ? (
          <p style={styles.empty}>No tienes transacciones todavía.</p>
        ) : (
          <div style={styles.grid}>
            {transacciones.map((t) => (
              <div key={t.id} style={styles.card}>
                <div style={styles.cardHeader}>
                  <h3 style={styles.cardTitle}>{t.productoNombre}</h3>
                  <span style={{ ...styles.badge, ...getBadgeStyle(t.estado) }}>
                    {t.estado}
                  </span>
                </div>

                <p style={styles.text}>
                  <strong>Propietario:</strong> {t.propietarioNombre}
                </p>
                <p style={styles.text}>
                  <strong>Comprador:</strong> {t.compradorNombre}
                </p>
                <p style={styles.text}>
                  <strong>Horas:</strong> {t.productoHoras}
                </p>

                <div style={styles.actions}>
                  {/* Botón de chat */}
                  <button
                    onClick={() => navigate(`/conversaciones/${t.id}`)}
                    style={styles.chatBtn}
                  >
                    💬 Ir al chat
                  </button>

                  {/* Finalizar (solo propietario en pendiente) */}
                  {t.estado === "PENDIENTE" &&
                    t.propietarioCorreo === user.correo && (
                      <button
                        onClick={() => handleFinalizar(t.id)}
                        style={styles.finalizarBtn}
                      >
                        ✅ Finalizar
                      </button>
                    )}

                  {/* Cancelar solicitud */}
                  {user.correo === t.propietarioCorreo &&
                    t.estado !== "FINALIZADA" && (
                      <button
                        onClick={async () => {
                          if (window.confirm("¿Seguro que quieres cancelar la solicitud?")) {
                            await cancelarSolicitud(t.id, token);
                            alert("✅ Solicitud cancelada");
                            const data = await obtenerMisTransacciones(token);
                            setTransacciones(data);
                          }
                        }}
                        style={styles.cancelBtn}
                      >
                        ❌ Cancelar
                      </button>
                    )}
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}

const styles = {
  layout: {
    display: "flex",
    minHeight: "100vh",
    backgroundColor: "#f9f9f9",
  },
  main: {
    flex: 1,
    padding: "30px",
    fontFamily: "Arial, sans-serif",
  },
  title: {
    fontSize: "24px",
    fontWeight: "bold",
    marginBottom: "20px",
    color: "#333",
  },
  empty: {
    textAlign: "center",
    fontSize: "16px",
    color: "#777",
    marginTop: "30px",
  },
  grid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(300px, 1fr))",
    gap: "20px",
  },
  card: {
    background: "#fff",
    borderRadius: "12px",
    boxShadow: "0 4px 8px rgba(0,0,0,0.08)",
    padding: "20px",
    transition: "transform 0.2s, box-shadow 0.2s",
  },
  cardHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "10px",
  },
  cardTitle: {
    margin: 0,
    fontSize: "18px",
    fontWeight: "600",
    color: "#222",
  },
  badge: {
    padding: "5px 12px",
    borderRadius: "20px",
    fontSize: "13px",
    fontWeight: "bold",
  },
  text: {
    fontSize: "14px",
    color: "#444",
    margin: "6px 0",
  },
  actions: {
    marginTop: "15px",
    display: "flex",
    flexWrap: "wrap",
    gap: "10px",
  },
  chatBtn: {
    flex: 1,
    padding: "8px 12px",
    backgroundColor: "#007bff",
    color: "#fff",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontWeight: "500",
  },
  finalizarBtn: {
    flex: 1,
    padding: "8px 12px",
    backgroundColor: "#28a745",
    color: "#fff",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontWeight: "500",
  },
  cancelBtn: {
    flex: 1,
    padding: "8px 12px",
    backgroundColor: "#dc3545",
    color: "#fff",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontWeight: "500",
  },
};
