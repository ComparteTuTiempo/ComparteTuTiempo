import React, { useEffect, useState } from "react";
import { useAuth } from "../utils/AuthContext";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/Sidebar";
import {
  obtenerMisTransacciones,
  finalizarTransaccion,
  cancelarSolicitud,
} from "../services/productoUsuarioService";

const estados = ["PENDIENTE", "FINALIZADA"];

const TransaccionesPage = () => {
  const { token, user } = useAuth();
  const navigate = useNavigate();
  const [tab, setTab] = useState("PENDIENTE");
  const [transacciones, setTransacciones] = useState([]);
  const [busqueda, setBusqueda] = useState(""); // <-- estado del buscador

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
      setTransacciones(prev =>
        prev.map(t => t.id === id ? { ...t, estado: "FINALIZADA" } : t)
      );
      alert("✅ Transacción finalizada");
    } catch (err) {
      alert("Error al finalizar: " + err.response?.data?.message);
    }
  };

  const handleCancelar = async (id) => {
    try {
      if (window.confirm("¿Seguro que quieres cancelar la solicitud?")) {
        await cancelarSolicitud(id, token);
        alert("✅ Solicitud cancelada");
        const data = await obtenerMisTransacciones(token);
        setTransacciones(data);
      }
    } catch (err) {
      alert("Error al cancelar: " + err.response?.data?.message);
    }
  };

  const getBadgeStyle = (estado) => {
    switch (estado) {
      case "PENDIENTE":
        return { backgroundColor: "#007bff", color: "#fff" };
      case "FINALIZADA":
        return { backgroundColor: "#28a745", color: "#fff" };
      default:
        return { backgroundColor: "#6c757d", color: "#fff" };
    }
  };

  // Filtrar según tab y búsqueda
  const transaccionesFiltradas = transacciones
    .filter(t => t.estado === tab)
    .filter(t => t.productoNombre.toLowerCase().includes(busqueda.toLowerCase()));

  return (
    <div style={styles.layout}>
      <Sidebar />
      <main style={styles.main}>
        <h2 style={styles.title}>📦 Mis Transacciones de Productos</h2>

        {/* Buscador */}
        <input
          type="text"
          placeholder="Buscar por nombre de intercambio..."
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          style={styles.searchInput}
        />

        {/* Tabs de estados */}
        <div style={styles.tabs}>
          {estados.map(e => (
            <button
              key={e}
              style={tab === e ? styles.activeTab : styles.tab}
              onClick={() => setTab(e)}
            >
              {e}
            </button>
          ))}
        </div>

        {/* Lista de transacciones */}
        <div style={styles.list}>
          {transaccionesFiltradas.length === 0 ? (
            <p style={styles.empty}>No hay transacciones en este estado.</p>
          ) : (
            transaccionesFiltradas.map(t => (
              <div key={t.id} style={styles.card}>
                <div style={styles.cardHeader}>
                  <h3 style={styles.cardTitle}>{t.productoNombre}</h3>
                  <span style={{ ...styles.badge, ...getBadgeStyle(t.estado) }}>
                    {t.estado}
                  </span>
                </div>

                <p style={styles.text}><strong>Propietario:</strong> {t.propietarioNombre}</p>
                <p style={styles.text}><strong>Comprador:</strong> {t.compradorNombre}</p>
                <p style={styles.text}><strong>Horas:</strong> {t.productoHoras}</p>

                <div style={styles.actions}>
                  <button
                    onClick={() => navigate(`/conversaciones/${t.id}`)}
                    style={styles.chatBtn}
                  >
                    💬 Ir al chat
                  </button>

                  {t.estado === "PENDIENTE" && t.propietarioCorreo === user.correo && (
                    <button
                      onClick={() => handleFinalizar(t.id)}
                      style={styles.finalizarBtn}
                    >
                      ✅ Finalizar
                    </button>
                  )}

                  {t.estado !== "FINALIZADA" && t.propietarioCorreo === user.correo && (
                    <button
                      onClick={() => handleCancelar(t.id)}
                      style={styles.cancelBtn}
                    >
                      ❌ Cancelar
                    </button>
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      </main>
    </div>
  );
};

const styles = {
  layout: { display: "flex", minHeight: "100vh", backgroundColor: "#f9f9f9" },
  main: { flex: 1, padding: "30px", fontFamily: "Arial, sans-serif" },
  title: { fontSize: "24px", fontWeight: "bold", marginBottom: "20px", color: "#333" },
  searchInput: {
    padding: "8px 12px",
    width: "100%",
    maxWidth: "400px",
    marginBottom: "15px",
    borderRadius: "6px",
    border: "1px solid #ccc",
    fontSize: "14px",
  },
  tabs: { display: "flex", gap: "10px", marginBottom: "20px", flexWrap: "wrap" },
  tab: {
    padding: "8px 16px",
    borderRadius: "6px",
    border: "1px solid #ddd",
    background: "#fff",
    cursor: "pointer",
  },
  activeTab: {
    padding: "8px 16px",
    borderRadius: "6px",
    border: "none",
    background: "#ff6f00",
    color: "#fff",
    fontWeight: "bold",
    cursor: "pointer",
  },
  list: { display: "grid", gap: "15px" },
  card: {
    background: "#fff",
    padding: "20px",
    borderRadius: "12px",
    boxShadow: "0 4px 8px rgba(0,0,0,0.08)",
  },
  cardHeader: { display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "10px" },
  cardTitle: { fontSize: "18px", fontWeight: "600", margin: 0 },
  badge: { padding: "5px 12px", borderRadius: "20px", fontSize: "13px", fontWeight: "bold" },
  text: { fontSize: "14px", color: "#444", margin: "6px 0" },
  actions: { marginTop: "15px", display: "flex", flexWrap: "wrap", gap: "10px" },
  chatBtn: { flex: 1, padding: "8px 12px", backgroundColor: "#007bff", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer" },
  finalizarBtn: { flex: 1, padding: "8px 12px", backgroundColor: "#28a745", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer" },
  cancelBtn: { flex: 1, padding: "8px 12px", backgroundColor: "#dc3545", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer" },
  empty: { textAlign: "center", fontSize: "16px", color: "#777", marginTop: "30px" },
};

export default TransaccionesPage;


