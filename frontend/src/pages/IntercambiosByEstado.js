import React, { useEffect, useState } from "react";
import { useAuth } from "../utils/AuthContext";
import {
  obtenerMisIntercambiosUsuario,
  finalizarAcuerdo,
} from "../services/intercambioService";
import { useNavigate } from "react-router-dom";
import SolicitudesIntercambio from "./SolicitudesIntercambioList"; 
import Sidebar from "../components/Sidebar";

const IntercambiosPorEstado = () => {
  const { token } = useAuth();
  const [intercambios, setIntercambios] = useState([]);
  const [seleccionado, setSeleccionado] = useState(null);
  const [tabActiva, setTabActiva] = useState("CONSENSO");
  const navigate = useNavigate();

  useEffect(() => {
    if (!token) return;

    const fetchIntercambios = async () => {
      try {
        if (tabActiva === "EMPAREJAMIENTO") return; 
        const data = await obtenerMisIntercambiosUsuario(tabActiva, token);
        setIntercambios(data);
      } catch (error) {
        console.error("Error al obtener intercambios:", error);
      }
    };

    fetchIntercambios();
  }, [token, tabActiva]);

  return (
    <div style={styles.layout}>
      <Sidebar />
      <main style={styles.main}>
        <h2 style={styles.pageTitle}>Mis Intercambios</h2>

        {/* Pestañas por estado */}
        <div style={styles.tabs}>
          {["EMPAREJAMIENTO", "CONSENSO", "EJECUCION"].map((estado) => (
            <button
              key={estado}
              onClick={() => setTabActiva(estado)}
              style={tabActiva === estado ? styles.activeTab : styles.tab}
            >
              {estado}
            </button>
          ))}
        </div>

        {/* Vista para cada estado */}
        {tabActiva === "EMPAREJAMIENTO" ? (
          <SolicitudesIntercambio /> 
        ) : (
          <div style={styles.cardsContainer}>
            {intercambios.length > 0 ? (
              intercambios.map((i) => (
                <div key={i.id} style={styles.card}>
                  <div style={styles.cardHeader}>
                    <h3 style={styles.cardTitle}>{i.intercambioNombre}</h3>
                    <span style={styles.badge}>{i.estado}</span>
                  </div>
                  <p style={styles.cardText}>
                    <strong>Solicitante:</strong> {i.solicitanteNombre}
                  </p>

                  {/* CONSENSO: botones chat y acuerdo */}
                  {i.estado === "CONSENSO" && (
                    <div style={styles.actions}>
                      {i.conversacionId && (
                        <button
                          onClick={() =>
                            navigate(`/conversaciones/${i.conversacionId}`)
                          }
                          style={styles.chatBtn}
                        >
                          💬 Discutir acuerdo
                        </button>
                      )}
                      <button
                        onClick={() => navigate(`/acuerdos/${i.id}`)}
                        style={styles.primaryBtn}
                      >
                        📑 Establecer acuerdo
                      </button>
                    </div>
                  )}

                  {/* EJECUCIÓN: botón finalizar */}
                  {tabActiva === "EJECUCION" && (
                    <button
                      onClick={async () => {
                        try {
                          await finalizarAcuerdo(i.id, token);
                          alert("✅ Acuerdo finalizado");
                          const data = await obtenerMisIntercambiosUsuario(
                            tabActiva,
                            token
                          );
                          setIntercambios(data);
                        } catch (err) {
                          alert("❌ Error al finalizar: " + err.message);
                        }
                      }}
                      style={styles.deleteBtn}
                    >
                      🔒 Finalizar
                    </button>
                  )}
                </div>
              ))
            ) : (
              <p>No tienes intercambios en este estado.</p>
            )}
          </div>
        )}

        {/* Modal opcional */}
        {seleccionado && (
          <div style={styles.modalOverlay}>
            <div style={styles.modalContent}>
              <button
                onClick={() => setSeleccionado(null)}
                style={styles.closeBtn}
              >
                ✖
              </button>
              <h2>{seleccionado.intercambioNombre}</h2>
              <p>
                <strong>Estado:</strong> {seleccionado.estado}
              </p>
              <p>
                <strong>Solicitante:</strong> {seleccionado.solicitanteNombre}
              </p>
              {seleccionado.estado === "CONSENSO" &&
                seleccionado.conversacionId && (
                  <button
                    onClick={() =>
                      navigate(`/conversaciones/${seleccionado.conversacionId}`)
                    }
                    style={styles.chatBtn}
                  >
                    💬 Ir al chat
                  </button>
                )}
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

const styles = {
  layout: { display: "flex", minHeight: "100vh", backgroundColor: "#f9f9f9" },
  main: { flex: 1, padding: "30px", fontFamily: "Arial, sans-serif" },
  pageTitle: { fontSize: "22px", fontWeight: "bold", marginBottom: "20px" },
  tabs: { display: "flex", gap: "20px", marginBottom: "20px" },
  tab: {
    background: "transparent",
    border: "none",
    fontSize: "16px",
    cursor: "pointer",
    color: "#666",
    paddingBottom: "6px",
  },
  activeTab: {
    background: "transparent",
    border: "none",
    fontSize: "16px",
    cursor: "pointer",
    color: "#ff6f00",
    paddingBottom: "6px",
    borderBottom: "2px solid #ff6f00",
    fontWeight: "bold",
  },
  cardsContainer: { display: "flex", flexDirection: "column", gap: "16px" },
  card: {
    background: "#fff",
    padding: "20px",
    borderRadius: "12px",
    boxShadow: "0 2px 6px rgba(0,0,0,0.08)",
  },
  cardHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "8px",
  },
  cardTitle: { fontSize: "18px", fontWeight: "bold", margin: 0 },
  badge: {
    fontSize: "12px",
    padding: "4px 8px",
    borderRadius: "6px",
    backgroundColor: "#f5f5f5",
    color: "#555",
    fontWeight: "500",
  },
  cardText: { fontSize: "14px", color: "#555", marginBottom: "12px" },
  actions: { display: "flex", gap: "10px", marginTop: "10px" },
  primaryBtn: {
    background: "#007bff",
    border: "none",
    color: "#fff",
    padding: "8px 14px",
    borderRadius: "6px",
    cursor: "pointer",
    fontWeight: "bold",
  },
  chatBtn: {
    background: "#28a745",
    border: "none",
    color: "#fff",
    padding: "8px 14px",
    borderRadius: "6px",
    cursor: "pointer",
    fontWeight: "bold",
  },
  deleteBtn: {
    marginTop: "10px",
    background: "#dc3545",
    border: "none",
    color: "#fff",
    padding: "8px 14px",
    borderRadius: "6px",
    cursor: "pointer",
    fontWeight: "bold",
  },
  modalOverlay: {
    position: "fixed",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: "rgba(0,0,0,0.5)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 1000,
  },
  modalContent: {
    background: "#fff",
    padding: "30px",
    borderRadius: "10px",
    width: "400px",
    position: "relative",
    boxShadow: "0 2px 8px rgba(0,0,0,0.2)",
  },
  closeBtn: {
    position: "absolute",
    top: "10px",
    right: "10px",
    border: "none",
    background: "none",
    fontSize: "18px",
    cursor: "pointer",
  },
};

export default IntercambiosPorEstado;
