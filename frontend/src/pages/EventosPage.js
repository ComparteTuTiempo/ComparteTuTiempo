import { useEffect, useState } from "react";
import { useAuth } from "../utils/AuthContext";
import { useNavigate } from "react-router-dom";
import {
  listarEventos,
  listarMisParticipaciones,
} from "../services/eventoService";
import Sidebar from "../components/Sidebar";
import EventoDetalle from "./EventoDetails";

const EventosPage = () => {
  const navigate = useNavigate();
  const { user, token } = useAuth();
  const [tab, setTab] = useState("all"); // "all" | "participaciones" | "misEventos"
  const [eventos, setEventos] = useState([]);
  const [selected, setSelected] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        if (tab === "all" || tab === "misEventos") {
          const data = await listarEventos();
          setEventos(data);
        } else if (tab === "participaciones" && user) {
          const data = await listarMisParticipaciones(user.correo, token);
          setEventos(data);
        }
      } catch (err) {
        console.error("❌ Error al cargar eventos:", err);
      }
    };
    fetchData();
  }, [tab, user, token]);

  // Separar
  const misEventos = eventos.filter(
    (e) => e.organizador.correo === user?.correo && e.estadoEvento != "FINALIZADO"
  );
  const otrosEventos = eventos.filter(
    (e) => e.organizador.correo !== user?.correo && e.estadoEvento != "FINALIZADO"
  );

  // Elegir qué mostrar
  const eventosMostrados =
    tab === "all" ? otrosEventos : tab === "misEventos" ? misEventos : eventos;

  return (
    <div style={styles.layout}>
      <Sidebar />
      <main style={styles.main}>
        <h2 style={{ marginBottom: "20px" }}>Eventos</h2>

        {/* Tabs */}
        <div style={styles.tabs}>
          <button
            onClick={() => setTab("all")}
            style={tab === "all" ? styles.activeTab : styles.tab}
          >
            Todos los eventos
          </button>
          <button
            onClick={() => setTab("participaciones")}
            style={tab === "participaciones" ? styles.activeTab : styles.tab}
          >
            Mis participaciones
          </button>
          <button
            onClick={() => setTab("misEventos")}
            style={tab === "misEventos" ? styles.activeTab : styles.tab}
          >
            Mis eventos
          </button>

          <button style={styles.createBtn} onClick={() => {navigate("/eventos/crear")}}>
            + Crear evento
          </button>
        </div>

        {/* Lista */}
        <div style={styles.list}>
          {eventosMostrados.length > 0 ? (
            eventosMostrados.map((e, idx) => (
              <div key={idx} style={styles.card}>
                <div style={styles.cardHeader}>
                  <h3 style={{ margin: 0 }}>{e.nombre}</h3>
                  <div style={styles.userBox}>
                    <img
                      src={
                        e.organizador?.fotoPerfil ||
                        "https://via.placeholder.com/32"
                      }
                      alt="user"
                      style={styles.avatar}
                    />
                    <span style={styles.username}>
                      {e.organizador?.nombre || e.organizador?.correo}
                    </span>
                  </div>
                </div>

                <div style={styles.metaInfo}>
                  <span style={styles.hours}>{e.duracion}h</span>
                  <span style={styles.modalidad}>{e.ubicacion}</span>
                  <span
                    style={{
                      ...styles.estado,
                      background:
                        e.estadoEvento === "FINALIZADO"
                          ? "#f00606ff"
                          : e.estadoEvento === "EN_CURSO"
                          ? "#ffc107"
                          : "#28a745",
                    }}
                  >
                    {e.estadoEvento}
                  </span>
                  <span style={styles.rating}>
                    {new Date(e.fechaEvento).toLocaleDateString()}
                  </span>
                  {tab === "participaciones" && (
                    <span
                      style={{
                        background: e.asistio ? "#28a745" : "#dc3545",
                        color: "#fff",
                        padding: "2px 8px",
                        borderRadius: "6px",
                        fontSize: "12px",
                      }}
                    >
                      {e.asistio ? "Asistí" : "No asistí"}
                    </span>
                  )}
                </div>

                <div style={styles.actions}>
                  <button style={styles.viewBtn} onClick={() => setSelected(e)}>
                    Ver más
                  </button>
                </div>
                {/* Modal detalle */}
                {selected && (
                    <div style={styles.modal}>
                    <div style={styles.modalContent}>
                        <button onClick={() => setSelected(null)} style={styles.closeBtn}>
                        ✖
                        </button>
                            <EventoDetalle id ={selected.id}/>
                    </div>
                    </div>
                )}
              </div>
              
              
            ))
          ) : (
            <p>No hay eventos</p>
          )}
          
        </div>
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
  },
  createBtn: {
  padding: "8px 16px",
  borderRadius: "6px",
  border: "none",
  background: "#28a745",
  color: "#fff",
  fontWeight: "bold",
  cursor: "pointer",
  transition: "background 0.3s",
  },
  cardHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "8px",
  },
  userBox: { display: "flex", alignItems: "center", gap: "6px" },
  avatar: {
    width: "32px",
    height: "32px",
    borderRadius: "50%",
    objectFit: "cover",
  },
  username: { fontSize: "14px", fontWeight: "500", color: "#333" },
  metaInfo: {
    display: "flex",
    gap: "10px",
    margin: "6px 0",
    fontSize: "13px",
    color: "#555",
  },
  estado: {
    padding: "2px 8px",
    borderRadius: "6px",
    fontSize: "12px",
    color: "#fff",
    fontWeight: "bold",
    textTransform: "capitalize",
  },
  hours: {
    background: "#f5f5f5",
    padding: "2px 8px",
    borderRadius: "6px",
    fontSize: "12px",
    fontWeight: "bold",
  },
  modalidad: {
    background: "#e9ecef",
    padding: "2px 8px",
    borderRadius: "6px",
    fontSize: "12px",
    fontWeight: "500",
  },
  rating: {
    background: "#f1f1f1",
    padding: "2px 8px",
    borderRadius: "6px",
    fontSize: "12px",
  },
  actions: { display: "flex", gap: "10px", marginTop: "10px" },
  viewBtn: {
    padding: "6px 12px",
    border: "1px solid #ddd",
    background: "#fff",
    borderRadius: "6px",
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

export default EventosPage;

