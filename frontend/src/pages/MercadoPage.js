import { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";

const Mercado = () => {
  const [productos, setProductos] = useState([]);
  const [selected, setSelected] = useState(null);
  const { user, token } = useAuth(); // 👈 para saber si es ADMIN

  // cargar productos
  useEffect(() => {
    axios
      .get(`${process.env.REACT_APP_API_URL}/productos`)
      .then((res) => setProductos(res.data))
      .catch((err) => console.error("❌ Error al cargar productos:", err));
  }, []);

  // eliminar producto (ADMIN)
  const eliminarProducto = async (id) => {
    if (!window.confirm("¿Seguro que quieres eliminar este producto?")) return;
    try {
      await axios.delete(`${process.env.REACT_APP_API_URL}/productos/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setProductos((prev) => prev.filter((p) => p.id !== id)); // actualizar lista
      setSelected(null);
      alert("✅ Producto eliminado con éxito");
    } catch (err) {
      console.error("❌ Error al eliminar producto:", err);
      alert("No se pudo eliminar el producto");
    }
  };

  return (
    <div style={styles.container}>
      <h2>Mercado de Productos</h2>

      <div style={styles.grid}>
        {productos.map((p) => (
          <div key={p.id} style={styles.card} onClick={() => setSelected(p)}>
            <h3>{p.nombre}</h3>
            <p>
              <strong>Horas:</strong> {p.numeroHoras}
            </p>
          </div>
        ))}
      </div>

      {selected && (
        <div style={styles.modal}>
          <div style={styles.modalContent}>
            <h3>{selected.nombre}</h3>
            <p>
              <strong>Descripción:</strong> {selected.descripcion}
            </p>
            <p>
              <strong>Horas:</strong> {selected.numeroHoras}
            </p>
            <p>
              <strong>Estado:</strong> {selected.estado}
            </p>
            <p>
              <strong>Publicado:</strong>{" "}
              {new Date(selected.fechaPublicacion).toLocaleDateString()}
            </p>

            {/* Botón de cierre */}
            <button onClick={() => setSelected(null)} style={styles.closeBtn}>
              Cerrar
            </button>

            {/* Botón eliminar visible solo para ADMIN */}
            {user?.roles?.includes("ADMIN") && (
              <button
                onClick={() => eliminarProducto(selected.id)}
                style={styles.deleteBtn}
              >
                🗑 Eliminar producto
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

const styles = {
  container: { padding: "20px", fontFamily: "Arial" },
  grid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))",
    gap: "20px",
    justifyContent: "start",
  },
  card: {
    backgroundColor: "#fdfdfd",
    padding: "12px",
    borderRadius: "8px",
    border: "1px solid #ddd",
    cursor: "pointer",
    fontSize: "14px",
    textAlign: "center",
    transition: "transform 0.2s, box-shadow 0.2s",
  },
  modal: {
    position: "fixed",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: "rgba(0,0,0,0.5)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    padding: "10px",
  },
  modalContent: {
    backgroundColor: "#fff",
    padding: "20px",
    borderRadius: "8px",
    maxWidth: "400px",
    width: "100%",
    textAlign: "left",
    fontSize: "14px",
  },
  closeBtn: {
    marginTop: "10px",
    padding: "8px 12px",
    border: "none",
    borderRadius: "6px",
    backgroundColor: "#6c757d",
    color: "#fff",
    fontSize: "13px",
    cursor: "pointer",
    marginRight: "10px",
  },
  deleteBtn: {
    marginTop: "10px",
    padding: "8px 12px",
    border: "none",
    borderRadius: "6px",
    backgroundColor: "#dc3545",
    color: "#fff",
    fontSize: "13px",
    cursor: "pointer",
  },
};

export default Mercado;
