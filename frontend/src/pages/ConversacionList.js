import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";
import { Link } from "react-router-dom";

export default function ConversationList() {
  const { user, token } = useAuth();
  const [convs, setConvs] = useState([]);
  const [search, setSearch] = useState("");

  useEffect(() => {
    if (!user?.correo) return;
    axios
      .get(`http://localhost:8080/conversaciones/user/${user.correo}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .then(res => setConvs(res.data))
      .catch(err => console.error(" Error al cargar conversaciones:", err));
  }, [user, token]);

  // filtrar conversaciones por búsqueda
  const filteredConvs = convs.filter(c => {
    const otro = c.participantes?.find(p => p.correo !== user?.correo);
    const nombreOtro = otro?.nombre?.toLowerCase() || "";
    const correoOtro = otro?.correo?.toLowerCase() || "";
    const titulo = c.titulo?.toLowerCase() || "";
    const query = search.toLowerCase();

    return (
      titulo.includes(query) ||
      nombreOtro.includes(query) ||
      correoOtro.includes(query)
    );
  });

  return (
    <div
      style={{
        maxWidth: "600px",
        margin: "20px auto",
        fontFamily: "Arial, sans-serif"
      }}
    >
      <h3 style={{ marginBottom: "15px" }}>Tus conversaciones</h3>

      {/* Campo de búsqueda */}
      <input
        type="text"
        placeholder="Buscar conversación..."
        value={search}
        onChange={e => setSearch(e.target.value)}
        style={{
          width: "100%",
          padding: "8px",
          marginBottom: "15px",
          border: "1px solid #ccc",
          borderRadius: "6px"
        }}
      />

      <ul style={{ listStyle: "none", padding: 0 }}>
        {filteredConvs.length === 0 ? (
          <p>No hay conversaciones que coincidan.</p>
        ) : (
          filteredConvs.map(c => {
            const otro = c.participantes?.find(p => p.correo !== user?.correo);
            const nombreMostrado = otro
              ? otro.nombre || otro.correo
              : `Chat #${c.id}`;

            return (
              <li
                key={c.id}
                style={{
                  padding: "10px 15px",
                  borderBottom: "1px solid #ddd",
                  cursor: "pointer"
                }}
              >
                <Link
                  to={`/conversaciones/${c.id}`}
                  style={{
                    textDecoration: "none",
                    color: "#007bff",
                    fontWeight: "bold"
                  }}
                >
                  {c.titulo || nombreMostrado}
                </Link>
              </li>
            );
          })
        )}
      </ul>
    </div>
  );
}

