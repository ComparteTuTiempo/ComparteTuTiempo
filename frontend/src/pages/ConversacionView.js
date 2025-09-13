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
  const scrollRef = useRef(null); 
  

  useEffect(() => {
    if (!id || !token || !user) return;

    axios.get(`http://localhost:8080/mensajes/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(res => setMessages(res.data))
    .catch(err => console.error(err));

    // Suscribirse al topic de conversación
    subRef.current = subscribe(`/topic/conversation/${id}`, (msg) => {
      setMessages(prev => [...prev, msg]);
    });

    return () => {
      if (subRef.current) subRef.current.unsubscribe();
    };
  }, [id, token, subscribe]);


  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages]);


  const handleSend = () => {
    if (!text.trim() || !user) return;


    const newMessage = {
      id: Math.random(), 
      contenido: text,
      remitente: user,
      timestamp: new Date().toISOString()
    };
    setMessages(prev => [...prev, newMessage]);


    send(`/app/chat/${id}`, { contenido: text });

    setText("");
  };

 
  if (!user) return <div>Cargando chat...</div>;

  return (
    <div style={styles.container}>
      <h3 style={styles.header}>Chat</h3>
      
      <div ref={scrollRef} style={styles.messagesContainer}>
        {messages.map((m, idx) => {       
          const isMe = user && m.remitente.correo?.toLowerCase() === user.correo?.toLowerCase();   
          return (
            
            <div key={m.id || idx} style={styles.messageWrapper(isMe)}>
              <div style={styles.messageBubble(isMe)}>
                <div style={styles.senderName}>{isMe ? "Tú" : m.senderNombre || "Desconocido"}</div>
                <div style={styles.messageContent}>{m.contenido}</div>
                <div style={styles.timestamp}>{new Date(m.timestamp).toLocaleTimeString()}</div>
              </div>
            </div>
          );
        })}
      </div>

      <div style={styles.inputContainer}>
        <textarea
          value={text}
          onChange={(e) => setText(e.target.value)}
          rows={2}
          style={styles.textarea}
          placeholder="Escribe un mensaje..."
        />
        <button onClick={handleSend} style={styles.sendButton}>Enviar</button>
      </div>
    </div>
  );
}

 const styles = {
    container: { maxWidth: 600, margin: "20px auto", fontFamily: "Arial, sans-serif" },
    header: { textAlign: "center" },
    messagesContainer: {
      height: 400,
      overflowY: "auto",
      border: "1px solid #ddd",
      borderRadius: 8,
      padding: 12,
      backgroundColor: "#f7f7f7",
      display: "flex",
      flexDirection: "column",
      gap: 8
    },
    messageWrapper: (isMe) => ({
      display: "flex",
      justifyContent: isMe ? "flex-end" : "flex-start"
    }),
    messageBubble: (isMe) => ({
      maxWidth: "70%",
      backgroundColor: isMe ? "#dcf8c6" : "#fff",
      padding: "8px 12px",
      borderRadius: 16,
      borderTopRightRadius: isMe ? 0 : 16,
      borderTopLeftRadius: isMe ? 16 : 0,
      boxShadow: "0 1px 2px rgba(0,0,0,0.1)"
    }),
    senderName: { fontSize: 12, color: "#555", marginBottom: 4 },
    messageContent: { fontSize: 14 },
    timestamp: { fontSize: 10, color: "#999", textAlign: "right", marginTop: 4 },
    inputContainer: { display: "flex", marginTop: 12 },
    textarea: {
      flex: 1,
      borderRadius: 8,
      border: "1px solid #ccc",
      padding: 8,
      resize: "none"
    },
    sendButton: {
      marginLeft: 8,
      padding: "8px 16px",
      borderRadius: 8,
      border: "none",
      backgroundColor: "#007bff",
      color: "#fff",
      cursor: "pointer"
    }
  };