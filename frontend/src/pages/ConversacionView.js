import React, { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";
import { useWebSocket } from "../utils/WebSocketProvider";

export default function ConversationView() {
  const { id } = useParams();
  const { token, user } = useAuth();
  const { subscribe, send } = useWebSocket();
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState("");
  const subRef = useRef(null);

  useEffect(() => {
    if (!id || !token) return;

    axios.get(`http://localhost:8080/mensajes/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    }).then(res => setMessages(res.data))
      .catch(err => console.error(err));

    // subscribe topic conversation
    subRef.current = subscribe(`/topic/conversation/${id}`, (msg) => {
      setMessages(prev => [...prev, msg]);
    });

    return () => {
      if (subRef.current) subRef.current.unsubscribe();
    };
  }, [id, token, subscribe]);

  const handleSend = () => {
    if (!text.trim()) return;
    send(`/app/chat/${id}`, { content: text });
    setText("");
  };

  return (
    <div>
      <h3>Chat</h3>
      <div style={{height: 400, overflow: "auto", border: "1px solid #ddd", padding: 8}}>
        {messages.map(m => (
          <div key={m.id || Math.random()} style={{marginBottom: 8}}>
            <strong>{m.senderNombre || m.senderCorreo}</strong>: {m.content}
            <div><small>{new Date(m.timestamp).toLocaleString()}</small></div>
          </div>
        ))}
      </div>

      <textarea value={text} onChange={(e)=>setText(e.target.value)} rows={3}/>
      <button onClick={handleSend}>Enviar</button>
    </div>
  );
}
