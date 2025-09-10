import React, { createContext, useContext, useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import { useAuth } from "../utils/AuthContext";
import SockJS from 'sockjs-client';

const WSContext = createContext();

export const WebSocketProvider = ({ children }) => {
  const { token } = useAuth();
  const clientRef = useRef(null);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    if (!token) return;

    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      brokerURL: "ws://localhost:8080/ws",
      connectHeaders: { Authorization: `Bearer ${token}` },
      debug: (str) => console.log("[STOMP]", str),
      reconnectDelay: 5000,
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
      clientRef.current = null;
    };
  }, [token]);

  const subscribe = (destination, callback) => {
    if (!clientRef.current || !clientRef.current.connected) {
      console.warn("⚠️ No conectado, no se pudo subscribir a", destination);
      return null;
    }
    return clientRef.current.subscribe(destination, (msg) => {
      try {
        const body = JSON.parse(msg.body);
        callback(body);
      } catch (err) {
        console.error("❌ Error parseando mensaje", err);
      }
    });
  };

  const send = (destination, body) => {
    if (!clientRef.current || !clientRef.current.connected) {
      throw new Error("⚠️ WebSocket no conectado");
    }
    clientRef.current.publish({
      destination,
      body: JSON.stringify(body),
    });
  };

  return (
    <WSContext.Provider value={{ connected, subscribe, send }}>
      {children}
    </WSContext.Provider>
  );
};

export const useWebSocket = () => useContext(WSContext);

