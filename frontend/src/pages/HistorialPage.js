import { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";

const HistorialPage = () => {
  const { token } = useAuth();
  const [tab, setTab] = useState("intercambios");
  const [intercambios, setIntercambios] = useState([]);
  const [productos, setProductos] = useState([]);
  const [selected, setSelected] = useState(null);

  useEffect(() => {
    // Cargar intercambios FINALIZADOS
    axios
      .get(`${process.env.REACT_APP_API_URL}/intercambios/historial`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setIntercambios(res.data))
      .catch((err) =>
        console.error("❌ Error al cargar historial de intercambios:", err)
      );

    // Cargar productos ENTREGADOS
    axios
      .get(`${process.env.REACT_APP_API_URL}/productos/historial`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setProductos(res.data))
      .catch((err) =>
        console.error("❌ Error al cargar historial de productos:", err)
      );
  }, [token]);

  const renderGrid = (items) => (
    <div style={styles.grid}>
      {items.map((item) => (
        <div
          key={item.id}
          style={styles.card}
          onClick={() => setSelected(item)}
        >
          <h3>{item.nombre}</h3>
          {item.numeroHoras && (
            <p>
              <strong>Horas:</strong> {item.numeroHoras}
            </p>
          )}
          {item.estado && (
            <p>
              <strong>Estado:</strong> {item.estado}
            </p>
          )}
        </div>
      ))}
    </div>
  );

  const renderModalContent = () => {
    if (!selected) return null;

    return (
      <div style={styles.modalContent}>
        <h3>{selected.nombre}</h3>
        {selected.descripcion && (
          <p>
            <strong>Descripción:</strong> {selected.descripcion}
          </p>
        )}
        {selected.numeroHoras && (
          <p>
            <strong>Horas:</strong> {selected.numeroHoras}
          </p>
        )}
        {selected.tipo && (
          <p>
            <strong>Tipo:</strong> {selected.tipo}
          </p>
        )}
        {selected.estado && (
          <p>
            <strong>Estado:</strong> {selected.estado}
          </p>
        )}
        {selected.fechaPublicacion && (
          <p>
            <strong>Publicado:</strong>{" "}
            {new Date(selected.fechaPublicacion).toLocaleDateString()}
          </p>
        )}
        {selected.fechaFinalizacion && (
          <p>
            <strong>Finalizado:</strong>{" "}
            {new Date(selected.fechaFinalizacion).toLocaleDateString()}
          </p>
        )}

        <button onClick={() => setSelected(null)} style={styles.closeBtn}>
          Cerrar
        </button>
      </div>
    );
  };

  return (
    <div style={styles.container}>
      <h2>Historial</h2>

      {/* Pestañas */}
      <div style={styles.tabs}>
        <button
          style={tab === "intercambios" ? styles.activeTab : styles.tab}
          onClick={() => setTab("intercambios")}
        >
          Intercambios
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
        {tab === "intercambios" && renderGrid(intercambios)}
        {tab === "productos" && renderGrid(productos)}
      </div>

      {/* Modal de detalles */}
      {selected && <div style={styles.modal}>{renderModalContent()}</div>}
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
  },
};

export default HistorialPage;
