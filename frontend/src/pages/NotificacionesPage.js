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
    <div className="max-w-3xl mx-auto p-6">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-bold">Tus notificaciones</h2>
        {notificaciones.length > 0 && (
          <button
            onClick={handleMarcarTodas}
            className="bg-orange-500 text-white px-4 py-2 rounded-md hover:bg-orange-600"
          >
            Marcar todas como leídas
          </button>
        )}
      </div>

      {notificaciones.length === 0 ? (
        <p className="text-gray-500">No tienes notificaciones</p>
      ) : (
        <ul className="space-y-3">
          {notificaciones.map(notif => (
            <li
              key={notif.id}
            >
              <p className="text-sm font-medium">{notif.contenido}</p>
              <span className="text-xs text-gray-400">
                {new Date(notif.timestamp).toLocaleString()}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
