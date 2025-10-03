import React, { useEffect, useState } from "react";
import { FaBell } from "react-icons/fa";
import { useWebSocket } from "../utils/WebSocketProvider";
import { useNavigate } from "react-router-dom";

const NotificacionesIcono = () => {
  const { subscribe, connected } = useWebSocket();
  const [notificaciones, setNotificaciones] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    if (!connected) return;
    const sub = subscribe("/user/queue/notifications", (notif) => {
      setNotificaciones((prev) => [notif, ...prev]);
    });
    return () => sub?.unsubscribe();
  }, [connected, subscribe]);

  const noLeidas = notificaciones.filter((n) => !n.leida).length;

  return (
    <div className="relative mx-3">
      <button onClick={() => navigate("/notificaciones")} className="relative">
        <FaBell className="w-6 h-6 text-orange-500 cursor-pointer" />
        {noLeidas > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs font-bold px-1.5 py-0.5 rounded-full">
            {noLeidas}
          </span>
        )}
      </button>
    </div>
  );
};


export default NotificacionesIcono;

