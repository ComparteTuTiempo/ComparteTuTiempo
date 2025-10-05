import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../utils/AuthContext";
import Sidebar from "../components/Sidebar";
import AvatarUsuario from "../components/AvatarUsuario";

const BuscarUsuarioPage = () => {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [nombre, setNombre] = useState("");
  const [resultados, setResultados] = useState([]);

  const handleSearch = async () => {
    if (!nombre.trim()) return;

    try {
      const res = await axios.get(
        `${process.env.REACT_APP_API_URL}/api/usuarios/buscar?nombre=${nombre}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setResultados(res.data);
    } catch (err) {
      console.error("❌ Error al buscar usuarios:", err);
    }
  };

  return (
     <div style={styles.layout}>
      <Sidebar />

      <main style={styles.main}>
        <h2 style={styles.title}>Buscar Usuario</h2>

        {/* Barra de búsqueda */}
        <div style={styles.searchBar}>
          <input
            type="text"
            placeholder="Nombre del usuario"
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
            style={styles.input}
          />
          <button onClick={handleSearch} style={styles.searchBtn}>
            Buscar
          </button>
        </div>

        {/* Resultados */}
        <ul style={styles.resultsList}>
          {resultados.map((u) => (
            <li
              key={u.correo}
              style={styles.resultItem}
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
      </main>
    </div>
  );
};

const styles = {
  layout: {
    display: "flex",
    minHeight: "100vh",
    backgroundColor: "#f8f9fa",
  },
  main: {
    flex: 1,
    padding: "30px",
    fontFamily: "Arial, sans-serif",
  },
  title: { marginBottom: "20px" },
  searchBar: { display: "flex", gap: "10px", marginBottom: "20px" },
  input: {
    flex: 1,
    padding: "8px",
    borderRadius: "6px",
    border: "1px solid #ccc",
  },
  searchBtn: {
    padding: "8px 12px",
    border: "none",
    backgroundColor: "#007bff",
    color: "#fff",
    borderRadius: "6px",
    cursor: "pointer",
  },
  resultsList: { listStyle: "none", padding: 0, margin: 0 },
  resultItem: {
    padding: "10px",
    borderBottom: "1px solid #ddd",
    display: "flex",
    alignItems: "center",
    gap: "10px",
    cursor: "pointer",
  },
};

export default BuscarUsuarioPage;
