import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";

const PublicacionesPage = () => {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [tab, setTab] = useState("ofertas");
  const [ofertas, setOfertas] = useState([]);
  const [peticiones, setPeticiones] = useState([]);
  const [productos, setProductos] = useState([]);
  const [selected, setSelected] = useState(null);

  useEffect(() => {
    // Obtener intercambios del usuario
    axios
      .get("http://localhost:8080/intercambios/usuario", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setOfertas(res.data.filter((i) => i.tipo === "OFERTA"));
        setPeticiones(res.data.filter((i) => i.tipo === "PETICION"));
      })
      .catch((err) => console.error("❌ Error al cargar intercambios:", err));

    // Obtener productos del usuario
    axios
      .get("http://localhost:8080/productos/usuario", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setProductos(res.data))
      .catch((err) => console.error("❌ Error al cargar productos:", err));
  }, [token]);

  const renderGrid = (items) => (
    <div style={styles.grid}>
      {items.map((p) => (
        <div key={p.id} style={styles.card} onClick={() => setSelected(p)}>
          <h3>{p.nombre}</h3>
          {"numeroHoras" in p && <p><strong>Horas:</strong> {p.numeroHoras}</p>}
          {"tipo" in p && <p><strong>Tipo:</strong> {p.tipo}</p>}
        </div>
      ))}
    </div>
  );

  const renderModalContent = () => {
    if (!selected) return null;

    const isProducto = selected.tipo === undefined; // Solo productos no tienen tipo OFERTA/PETICION

    return (
      <div style={styles.modalContent}>
        <h3>{selected.nombre}</h3>
        {selected.descripcion && <p><strong>Descripción:</strong> {selected.descripcion}</p>}
        {selected.numeroHoras && <p><strong>Horas:</strong> {selected.numeroHoras}</p>}
        {selected.tipo && <p><strong>Tipo:</strong> {selected.tipo}</p>}
        {selected.estado && <p><strong>Estado:</strong> {selected.estado}</p>}
        {selected.fechaPublicacion && (
          <p><strong>Publicado:</strong> {new Date(selected.fechaPublicacion).toLocaleDateString()}</p>
        )}

        {isProducto && (
          <div style={{ marginTop: "15px", display: "flex", gap: "10px" }}>
            <button
              style={styles.editBtn}
              onClick={() => navigate(`/productos/editar/${selected.id}`)}
            >
              Editar
            </button>
            <button
              style={styles.deleteBtn}
              onClick={async () => {
                if (window.confirm("¿Seguro que quieres eliminar este producto?")) {
                  try {
                    await axios.delete(`http://localhost:8080/productos/${selected.id}`, {
                      headers: { Authorization: `Bearer ${token}` },
                    });
                    alert("Producto eliminado ✅");
                    setSelected(null);
                    setProductos(productos.filter(p => p.id !== selected.id));
                  } catch (err) {
                    console.error("❌ Error al eliminar producto:", err);
                    alert("Hubo un error al eliminar el producto");
                  }
                }
              }}
            >
              Eliminar
            </button>
          </div>
        )}

        <button onClick={() => setSelected(null)} style={styles.closeBtn}>
          Cerrar
        </button>
      </div>
    );
  };

  return (
    <div style={styles.container}>
      <h2>Mis Publicaciones</h2>

      {/* Pestañas */}
      <div style={styles.tabs}>
        <button
          style={tab === "ofertas" ? styles.activeTab : styles.tab}
          onClick={() => setTab("ofertas")}
        >
          Ofertas
        </button>
        <button
          style={tab === "peticiones" ? styles.activeTab : styles.tab}
          onClick={() => setTab("peticiones")}
        >
          Peticiones
        </button>
        <button
          style={tab === "productos" ? styles.activeTab : styles.tab}
          onClick={() => setTab("productos")}
        >
          Productos
        </button>
      </div>

      {/* Contenido */}
      <div style={styles.content}>
        {tab === "ofertas" && renderGrid(ofertas)}
        {tab === "peticiones" && renderGrid(peticiones)}
        {tab === "productos" && renderGrid(productos)}
      </div>

      {/* Modal de detalles */}
      {selected && (
        <div style={styles.modal}>
          {renderModalContent()}
        </div>
      )}
    </div>
  );
};

const styles = {
  container: { padding: "20px", fontFamily: "Arial, sans-serif" },
  tabs: { display: "flex", gap: "10px", marginBottom: "20px" },
  tab: {
    padding: "10px 15px",
    backgroundColor: "#f0f0f0",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
  },
  activeTab: {
    padding: "10px 15px",
    backgroundColor: "#007bff",
    color: "#fff",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
  },
  content: {},
  grid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(180px, 1fr))",
    gap: "20px",
    justifyContent: "start",
  },
  card: {
    backgroundColor: "#fdfdfd",
    padding: "12px",
    borderRadius: "8px",
    border: "1px solid #ddd",
    fontSize: "14px",
    textAlign: "center",
    cursor: "pointer",
    transition: "transform 0.2s, box-shadow 0.2s",
  },
  modal: {
    position: "fixed",
    top: 0, left: 0, right: 0, bottom: 0,
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
  },
  editBtn: {
    padding: "8px 12px",
    borderRadius: "6px",
    backgroundColor: "#28a745",
    color: "#fff",
    border: "none",
    cursor: "pointer",
  },
  deleteBtn: {
    padding: "8px 12px",
    borderRadius: "6px",
    backgroundColor: "#dc3545",
    color: "#fff",
    border: "none",
    cursor: "pointer",
  },
};

export default PublicacionesPage;
