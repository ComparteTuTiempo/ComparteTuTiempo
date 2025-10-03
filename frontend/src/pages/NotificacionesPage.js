import { useEffect, useState } from "react";
import {
  getNotificaciones,
  marcarComoLeida,
  marcarTodasComoLeidas,
} from "../services/notificacionService";
import { useWebSocket } from "../utils/WebSocketProvider";
import { useAuth } from "../utils/AuthContext";

export default function NotificacionesPage() {
  const [notificaciones, setNotificaciones] = useState([]);
  const { token } = useAuth();
  const { subscribe, connected } = useWebSocket();

  // Cargar notificaciones iniciales
  useEffect(() => {
    if (!token) return;
    getNotificaciones(token)
      .then((data) => setNotificaciones(data))
      .catch((err) => console.error(err));
  }, [token]);

  // Suscribirse a notificaciones en tiempo real
  useEffect(() => {
    if (!connected) return;
    const sub = subscribe("/user/queue/notifications", (notif) => {
      setNotificaciones((prev) => [notif, ...prev]);
    });
    return () => sub?.unsubscribe();
  }, [connected, subscribe]);

  const handleMarcarTodas = () => {
    marcarTodasComoLeidas(token).then(() => {
      setNotificaciones([]);
    });
  };

 
  return (
    <div style={styles.container}>
      {/* Header */}
      <div style={styles.header}>
        <h2 style={styles.title}>🔔 Tus notificaciones</h2>
        {notificaciones.length > 0 && (
          <button onClick={handleMarcarTodas} style={styles.btn}>
            Marcar todas como leídas
          </button>
        )}
      </div>

      {/* Lista */}
      {notificaciones.length === 0 ? (
        <p style={styles.empty}>No tienes notificaciones</p>
      ) : (
        <ul style={styles.list}>
          {notificaciones.map((notif) => (
            <li key={notif.id} style={styles.card}>
              <p style={styles.content}>{notif.contenido}</p>
              <span style={styles.date}>
                {new Date(notif.timestamp).toLocaleString()}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

const styles = {
  container: {
    maxWidth: "700px",
    margin: "0 auto",
    padding: "20px",
    fontFamily: "Arial, sans-serif",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "20px",
  },
  title: {
    fontSize: "22px",
    fontWeight: "bold",
  },
  btn: {
    backgroundColor: "#ff6f00",
    color: "#fff",
    border: "none",
    padding: "8px 15px",
    borderRadius: "6px",
    cursor: "pointer",
    fontWeight: "bold",
  },
  empty: {
    textAlign: "center",
    color: "#666",
    marginTop: "50px",
  },
  list: {
    listStyle: "none",
    padding: 0,
    margin: 0,
    display: "flex",
    flexDirection: "column",
    gap: "15px",
  },
  card: {
    backgroundColor: "#fff",
    border: "1px solid #ddd",
    borderRadius: "10px",
    padding: "15px",
    boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
  },
  content: {
    fontSize: "15px",
    fontWeight: "500",
    color: "#333",
    marginBottom: "8px",
  },
  date: {
    fontSize: "12px",
    color: "#888",
    textAlign: "right",
    display: "block",
  },
};