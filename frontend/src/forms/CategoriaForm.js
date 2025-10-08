import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";

const CategoriasAdminPage = () => {
  const { token } = useAuth();
  const [categorias, setCategorias] = useState([]);
  const [nueva, setNueva] = useState({ nombre: "" });
  const [editando, setEditando] = useState(null);

  const fetchCategorias = async () => {
    try {
      const res = await axios.get(`http://localhost:8080/categorias`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setCategorias(res.data);
    } catch (err) {
      console.error("❌ Error al cargar categorías:", err);
    }
  };

  useEffect(() => {
    fetchCategorias();
  }, []);

  const handleCrear = async () => {
    try {
      await axios.post(`http://localhost:8080/categorias`, nueva, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setNueva({ nombre: "" });
      fetchCategorias();
    } catch (err) {
      console.error("❌ Error al crear categoría:", err);
    }
  };

  const handleActualizar = async (id) => {
    try {
      await axios.put(`http://localhost:8080/categorias/${id}`, editando, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setEditando(null);
      fetchCategorias();
    } catch (err) {
      console.error("❌ Error al actualizar categoría:", err);
    }
  };

  const handleEliminar = async (id) => {
    if (!window.confirm("¿Seguro que quieres eliminar esta categoría?")) return;
    try {
      await axios.delete(`http://localhost:8080/categorias/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchCategorias();
    } catch (err) {
      console.error("❌ Error al eliminar categoría:", err);
    }
  };

  return (
    <div style={styles.container}>
      <h2>Administrar Categorías</h2>

      {/* Crear nueva */}
      <div style={styles.form}>
        <input
          type="text"
          placeholder="Nombre"
          value={nueva.nombre}
          onChange={(e) => setNueva({ nombre: e.target.value })}
          style={styles.input}
        />
        <button style={styles.addBtn} onClick={handleCrear}>
          ➕ Crear
        </button>
      </div>

      {/* Listado */}
      <ul style={styles.list}>
        {categorias.map((c) => (
          <li key={c.id} style={styles.listItem}>
            {editando?.id === c.id ? (
              <>
                <input
                  type="text"
                  value={editando.nombre}
                  onChange={(e) =>
                    setEditando({ ...editando, nombre: e.target.value })
                  }
                  style={styles.input}
                />
                <button
                  style={styles.saveBtn}
                  onClick={() => handleActualizar(c.id)}
                >
                  💾 Guardar
                </button>
                <button style={styles.cancelBtn} onClick={() => setEditando(null)}>
                  Cancelar
                </button>
              </>
            ) : (
              <>
                <strong>{c.nombre}</strong>
                <div style={styles.actions}>
                  <button style={styles.editBtn} onClick={() => setEditando(c)}>
                    ✏ Editar
                  </button>
                  <button
                    style={styles.deleteBtn}
                    onClick={() => handleEliminar(c.id)}
                  >
                    🗑 Eliminar
                  </button>
                </div>
              </>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

const styles = {
  container: { padding: "20px" },
  form: { display: "flex", gap: "10px", marginBottom: "20px" },
  input: { padding: "6px", border: "1px solid #ccc", borderRadius: "6px" },
  addBtn: {
    backgroundColor: "#28a745",
    border: "none",
    color: "#fff",
    padding: "6px 12px",
    borderRadius: "6px",
    cursor: "pointer",
  },
  list: { listStyle: "none", padding: 0 },
  listItem: {
    marginBottom: "10px",
    background: "#fff",
    padding: "10px",
    borderRadius: "6px",
    boxShadow: "0 1px 4px rgba(0,0,0,0.1)",
  },
  actions: { marginTop: "5px" },
  editBtn: {
    marginRight: "5px",
    backgroundColor: "#007bff",
    color: "white",
    border: "none",
    padding: "4px 8px",
    borderRadius: "4px",
    cursor: "pointer",
  },
  deleteBtn: {
    backgroundColor: "#dc3545",
    color: "white",
    border: "none",
    padding: "4px 8px",
    borderRadius: "4px",
    cursor: "pointer",
  },
  saveBtn: {
    backgroundColor: "#28a745",
    color: "white",
    border: "none",
    padding: "4px 8px",
    borderRadius: "4px",
    cursor: "pointer",
  },
  cancelBtn: {
    backgroundColor: "#6c757d",
    color: "white",
    border: "none",
    padding: "4px 8px",
    borderRadius: "4px",
    cursor: "pointer",
  },
};

export default CategoriasAdminPage;
