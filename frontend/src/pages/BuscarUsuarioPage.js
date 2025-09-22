import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../utils/AuthContext";
import AvatarUsuario from "../components/AvatarUsuario"; // 👈 importa el avatar

const BuscarUsuarioPage = () => {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [nombre, setNombre] = useState("");
  const [resultados, setResultados] = useState([]);

  const handleSearch = async () => {
    if (!nombre.trim()) return;

    try {
      const res = await axios.get(
        `http://localhost:8080/api/usuarios/buscar?nombre=${nombre}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setResultados(res.data);
    } catch (err) {
      console.error("❌ Error al buscar usuarios:", err);
    }
  };

  return (
    <div style={{ padding: "20px", fontFamily: "Arial, sans-serif" }}>
      <h2>Buscar Usuario</h2>

      {/* Barra de búsqueda */}
      <div style={{ display: "flex", gap: "10px", marginBottom: "20px" }}>
        <input
          type="text"
          placeholder="Nombre del usuario"
          value={nombre}
          onChange={(e) => setNombre(e.target.value)}
          style={{ flex: 1, padding: "8px" }}
        />
        <button
          onClick={handleSearch}
          style={{
            padding: "8px 12px",
            border: "none",
            backgroundColor: "#007bff",
            color: "#fff",
            borderRadius: "6px",
            cursor: "pointer",
          }}
        >
          Buscar
        </button>
      </div>

      {/* Resultados */}
      <ul style={{ listStyle: "none", padding: 0 }}>
        {resultados.map((u) => (
          <li
            key={u.correo}
            style={{
              padding: "10px",
              borderBottom: "1px solid #ddd",
              display: "flex",
              alignItems: "center",
              cursor: "pointer",
            }}
            onClick={() => navigate(`/perfil/${u.correo}`)}
          >
            <AvatarUsuario src={u.fotoPerfil} alt={u.nombre} size={40} />
            <div>
              <strong>{u.nombre}</strong>
              <div style={{ fontSize: "12px", color: "#555" }}>
                {u.ubicacion || "Ubicación no especificada"}
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default BuscarUsuarioPage;
