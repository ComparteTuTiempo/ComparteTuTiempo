import React, { useEffect, useState } from "react";
import { useAuth } from "../utils/AuthContext";
import { useNavigate } from "react-router-dom";
import {
  obtenerMisIntercambiosUsuario,
  finalizarAcuerdo,
} from "../services/intercambioService";
import Sidebar from "../components/Sidebar";
import DetalleIntercambioUsuario from "./IntercambioUsuarioDetails";
import SolicitudesIntercambio from "./SolicitudesIntercambioList";

const IntercambiosByEstado = () => {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [tab, setTab] = useState("CONSENSO"); 
  const [intercambios, setIntercambios] = useState([]);
  const [selected, setSelected] = useState(null);

  useEffect(() => {
    if (!token) return;

    const fetchIntercambios = async () => {
      try {
        if (tab === "EMPAREJAMIENTO") return; // las solicitudes se manejan aparte
        const data = await obtenerMisIntercambiosUsuario(tab, token);
        setIntercambios(data);
      } catch (err) {
        console.error("❌ Error al cargar intercambios:", err);
      }
    };
    fetchIntercambios();
  }, [tab, token]);

  const handleFinalizar = async (id) => {
    try {
      await finalizarAcuerdo(id, token);
      alert("✅ Intercambio finalizado");
      const data = await obtenerMisIntercambiosUsuario(tab, token);
      setIntercambios(data);
    } catch (err) {
      alert("❌ Error al finalizar: " + (err.response?.data?.message || err.message));
    }
  };

  const handleEstablecerConsenso = (id) => {
    navigate(`/acuerdos/${id}`);
  };

  const getEstadoStyle = (estado) => {
    switch (estado) {
      case "FINALIZADO": return { background: "#f01202ff" };
      case "EJECUCION": return { background: "#96a728ff" };
      case "CONSENSO": return { background: "#007bff" };
      case "EMPAREJAMIENTO": return { background: "#ffc107" };
      default: return { background: "#6c757d" };
    }
  };

  return (
    <div style={styles.layout}>
      <Sidebar />
      <main style={styles.main}>
        <h2 style={{ marginBottom: "20px" }}>Mis Intercambios</h2>

        {/* Tabs */}
        <div style={styles.tabs}>
          {["EMPAREJAMIENTO", "CONSENSO", "EJECUCION", "FINALIZADO"].map((estado) => (
            <button
              key={estado}
              onClick={() => setTab(estado)}
              style={tab === estado ? styles.activeTab : styles.tab}
            >
              {estado}
            </button>
          ))}
        </div>

        {/* EMPAREJAMIENTO: solicitudes */}
        {tab === "EMPAREJAMIENTO" && <SolicitudesIntercambio />}

        {/* Grid de intercambios */}
        {tab !== "EMPAREJAMIENTO" && (
          <div style={{ ...styles.list, gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))" }}>
            {intercambios.length > 0 ? (
              intercambios.map((i) => (
                <div key={i.id} style={styles.card}>
                  <div style={styles.cardHeader}>
                    <h3 style={{ margin: 0 }}>{i.intercambioNombre}</h3>
                    <span style={{ ...styles.estado, ...getEstadoStyle(i.estado) }}>
                      {i.estado}
                    </span>
                  </div>
                  <p style={{ margin: "8px 0 0 0" }}>
                    <strong>Solicitante:</strong> {i.solicitanteNombre || i.solicitanteCorreo}
                  </p>
                  <p><strong>Horas asignadas:</strong> {i.horasAsignadas}</p>

                  <p><strong>Tipo:</strong> {i.tipo}</p>

                  <div style={styles.actions}>
                    <button style={styles.viewBtn} onClick={() => setSelected(i)}>
                      Ver detalles
                    </button>

                    {tab === "EJECUCION" && (
                      <button style={styles.finalizarBtn} onClick={() => handleFinalizar(i.id)}>
                        Finalizar
                      </button>
                    )}

                    {tab === "CONSENSO" && (
                      <>
                        {i.conversacionId && (
                          <button
                            style={styles.chatBtn}
                            onClick={() => navigate(`/conversaciones/${i.conversacionId}`)}
                          >
                            💬 Discutir acuerdo
                          </button>
                        )}
                        <button
                          style={styles.consensoBtn}
                          onClick={() => handleEstablecerConsenso(i.id)}
                        >
                          Establecer Consenso
                        </button>
                      </>
                    )}
                  </div>
                </div>
              ))
            ) : (
              <p>No hay intercambios</p>
            )}
          </div>
        )}

        {/* Modal detalle */}
        {selected && (
          <div style={styles.modal}>
            <div style={styles.modalContent}>
              <button onClick={() => setSelected(null)} style={styles.closeBtn}>✖</button>
              <DetalleIntercambioUsuario id={selected.id} />
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
  tabs: { display: "flex", gap: "10px", marginBottom: "20px" },
  tab: {
    padding: "10px 15px",
    border: "none",
    borderRadius: "6px",
    background: "#eee",
    cursor: "pointer",
  },
  activeTab: {
    padding: "10px 15px",
    border: "none",
    borderRadius: "6px",
    background: "#ff6f00",
    color: "#fff",
    fontWeight: "bold",
  },
  list: { display: "grid", gap: "15px" },
  card: {
    background: "#fff",
    padding: "15px",
    borderRadius: "10px",
    boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
    display: "flex",
    flexDirection: "column",
  },
  cardHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "8px",
  },
  estado: {
    padding: "2px 8px",
    borderRadius: "6px",
    fontSize: "12px",
    color: "#fff",
    fontWeight: "bold",
    textTransform: "capitalize",
  },
  actions: { display: "flex", gap: "10px", marginTop: "10px", flexWrap: "wrap" },
  viewBtn: {
    padding: "6px 12px",
    border: "1px solid #ddd",
    background: "#fff",
    borderRadius: "6px",
    cursor: "pointer",
  },
  finalizarBtn: {
    padding: "6px 12px",
    border: "none",
    borderRadius: "6px",
    background: "#28a745",
    color: "#fff",
    cursor: "pointer",
  },
  consensoBtn: {
    padding: "6px 12px",
    border: "none",
    borderRadius: "6px",
    background: "#007bff",
    color: "#fff",
    cursor: "pointer",
  },
  chatBtn: {
    padding: "6px 12px",
    border: "none",
    borderRadius: "6px",
    background: "#28a745",
    color: "#fff",
    cursor: "pointer",
  },
  modal: {
    position: "fixed",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background: "rgba(0,0,0,0.5)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 1000,
    overflowY: "auto",
  },
  modalContent: {
    background: "#fff",
    padding: "20px",
    borderRadius: "10px",
    maxWidth: "900px",
    width: "95%",
    maxHeight: "80vh",
    overflowY: "auto",
    position: "relative",
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

export default IntercambiosByEstado;