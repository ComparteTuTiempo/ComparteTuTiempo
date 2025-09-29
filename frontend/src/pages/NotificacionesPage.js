import { useEffect, useState } from "react";
import {
  getNotificaciones,
  marcarComoLeida,
  marcarTodasComoLeidas,
} from "../services/notificacionService";

export default function NotificacionesPage() {
  const [notificaciones, setNotificaciones] = useState([]);

  useEffect(() => {
    getNotificaciones()
      .then((res) => setNotificaciones(res.data))
      .catch((err) => console.error("❌ Error cargando notificaciones", err));
  }, []);

  const handleMarcarLeida = (id) => {
    marcarComoLeida(id).then(() => {
      setNotificaciones((prev) =>
        prev.map((n) =>
          n.id === id ? { ...n, leida: true } : n
        )
      );
    });
  };

  const handleMarcarTodas = () => {
    marcarTodasComoLeidas().then(() => {
      setNotificaciones((prev) => prev.map((n) => ({ ...n, leida: true })));
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
          {notificaciones.map((notif) => (
            <li
              key={notif.id}
              className={`p-4 rounded-lg border cursor-pointer ${
                notif.leida ? "bg-white" : "bg-orange-50"
              }`}
              onClick={() => handleMarcarLeida(notif.id)}
            >
              <p className="text-sm">{notif.contenido}</p>
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
