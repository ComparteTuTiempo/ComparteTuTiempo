import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";
import { Link } from "react-router-dom";

export default function ConversationList() {
  const { user, token } = useAuth();
  const [convs, setConvs] = useState([]);

  useEffect(() => {
    if (!user?.correo) return;
    axios
      .get(`http://localhost:8080/conversaciones/user/${user.correo}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .then(res => setConvs(res.data))
      .catch(err => console.error("❌ Error al cargar conversaciones:", err));
  }, [user, token]);

  return (
    <div style={{ maxWidth: "600px", margin: "20px auto", fontFamily: "Arial, sans-serif" }}>
      <h3 style={{ marginBottom: "15px" }}>Tus conversaciones</h3>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {convs.length === 0 ? (
          <p>No tienes conversaciones abiertas.</p>
        ) : (
          convs.map(c => {
            // sacar el otro usuario (distinto al logueado)
            const otro = c.participantes?.find(p => p.correo !== user?.correo);
            const nombreMostrado = otro ? (otro.nombre || otro.correo) : `Chat #${c.id}`;

            return (
              <li
                key={c.id}
                style={{
                  padding: "10px 15px",
                  borderBottom: "1px solid #ddd",
                  cursor: "pointer",
                }}
              >
                <Link
                  to={`/conversaciones/${c.id}`}
                  style={{ textDecoration: "none", color: "#007bff", fontWeight: "bold" }}
                >
                  {nombreMostrado}
                </Link>
              </li>
            );
          })
        )}
      </ul>
    </div>
  );
}
