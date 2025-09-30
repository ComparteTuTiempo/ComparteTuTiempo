import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";
import Sidebar from "../components/Sidebar";

const PublicacionesPage = () => {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [tab, setTab] = useState("ofertas");
  const [ofertas, setOfertas] = useState([]);
  const [peticiones, setPeticiones] = useState([]);
  const [productos, setProductos] = useState([]);

  useEffect(() => {
    // intercambios del usuario
    axios
      .get("http://localhost:8080/intercambios/usuario", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setOfertas(res.data.filter((i) => i.tipo === "OFERTA"));
        setPeticiones(res.data.filter((i) => i.tipo === "PETICION"));
      })
      .catch((err) => console.error("❌ Error intercambios:", err));

    // productos del usuario
    axios
      .get("http://localhost:8080/productos/usuario", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setProductos(res.data))
      .catch((err) => console.error("❌ Error productos:", err));
  }, [token]);

  const renderCards = (items, type) => (
    <div style={styles.cardsContainer}>
      {items.length > 0 ? (
        items.map((p) => (
          <div key={p.id} style={styles.card}>
            {/* Header: título + etiqueta */}
            <div style={styles.cardHeader}>
              <h3 style={styles.cardTitle}>{p.nombre}</h3>
              <span style={styles.badge}>
                {type === "oferta"
                  ? "Service Offer"
                  : type === "peticion"
                  ? "Service Request"
                  : "Product"}
              </span>
            </div>

            {/* Descripción */}
            <p style={styles.cardText}>{p.descripcion || "Sin descripción"}</p>

            {/* Footer con avatar + botones */}
            <div style={styles.cardFooter}>
              <img
                src={p.user?.fotoPerfil || "https://via.placeholder.com/30"}
                alt="user"
                style={styles.avatar}
              />
              <div style={styles.actions}>
                <button
                  style={styles.editBtn}
                  onClick={() =>
                    type === "producto"
                      ? navigate(`/productos/editar/${p.id}`)
                      : navigate(`/intercambios/${p.id}/editar`)
                  }
                >
                  Edit
                </button>
                <button
                  style={styles.deleteBtn}
                  onClick={async () => {
                    if (window.confirm("¿Seguro que quieres eliminarlo?")) {
                      try {
                        if (type === "producto") {
                          await axios.delete(`http://localhost:8080/productos/${p.id}`, {
                            headers: { Authorization: `Bearer ${token}` },
                          });
                          setProductos(productos.filter((x) => x.id !== p.id));
                        } else {
                          await axios.delete(`http://localhost:8080/intercambios/${p.id}`, {
                            headers: { Authorization: `Bearer ${token}` },
                          });
                          if (type === "oferta") {
                            setOfertas(ofertas.filter((x) => x.id !== p.id));
                          } else {
                            setPeticiones(peticiones.filter((x) => x.id !== p.id));
                          }
                        }
                        alert("✅ Eliminado con éxito");
                      } catch (err) {
                        console.error("❌ Error al eliminar:", err);
                        alert("Hubo un error al eliminar");
                      }
                    }
                  }}
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
        ))
      ) : (
        <p>No hay publicaciones</p>
      )}
    </div>
  );

  return (
    <div style={styles.layout}>
      <Sidebar />

      <main style={styles.main}>
        <h2 style={styles.pageTitle}>My Publications</h2>

        {/* Tabs */}
        <div style={styles.tabs}>
          <button
            style={tab === "ofertas" ? styles.activeTab : styles.tab}
            onClick={() => setTab("ofertas")}
          >
            Offers
          </button>
          <button
            style={tab === "peticiones" ? styles.activeTab : styles.tab}
            onClick={() => setTab("peticiones")}
          >
            Requests
          </button>
          <button
            style={tab === "productos" ? styles.activeTab : styles.tab}
            onClick={() => setTab("productos")}
          >
            Products
          </button>
        </div>

        {/* Contenido */}
        {tab === "ofertas" && renderCards(ofertas, "oferta")}
        {tab === "peticiones" && renderCards(peticiones, "peticion")}
        {tab === "productos" && renderCards(productos, "producto")}
      </main>
    </div>
  );
};

const styles = {
  layout: { display: "flex", minHeight: "100vh", backgroundColor: "#f9f9f9" },
  main: { flex: 1, padding: "30px", fontFamily: "Arial, sans-serif" },
  pageTitle: { fontSize: "22px", fontWeight: "bold", marginBottom: "20px" },
  tabs: { display: "flex", gap: "20px", marginBottom: "20px" },
  tab: {
    background: "transparent",
    border: "none",
    fontSize: "16px",
    cursor: "pointer",
    color: "#666",
    paddingBottom: "6px",
  },
  activeTab: {
    background: "transparent",
    border: "none",
    fontSize: "16px",
    cursor: "pointer",
    color: "#ff6f00",
    paddingBottom: "6px",
    borderBottom: "2px solid #ff6f00",
    fontWeight: "bold",
  },
  cardsContainer: { display: "flex", flexDirection: "column", gap: "16px" },
  card: {
    background: "#fff",
    padding: "20px",
    borderRadius: "12px",
    boxShadow: "0 2px 6px rgba(0,0,0,0.08)",
  },
  cardHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "8px",
  },
  cardTitle: { fontSize: "18px", fontWeight: "bold", margin: 0 },
  badge: {
    fontSize: "12px",
    padding: "4px 8px",
    borderRadius: "6px",
    backgroundColor: "#f5f5f5",
    color: "#555",
    fontWeight: "500",
  },
  cardText: { fontSize: "14px", color: "#555", marginBottom: "12px" },
  cardFooter: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  avatar: {
    width: "28px",
    height: "28px",
    borderRadius: "50%",
    objectFit: "cover",
  },
  actions: { display: "flex", gap: "10px" },
  editBtn: {
    background: "#fff",
    border: "1px solid #ddd",
    padding: "6px 14px",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
  },
  deleteBtn: {
    background: "#ff6f00",
    border: "none",
    color: "#fff",
    padding: "6px 14px",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: "bold",
  },
};

export default PublicacionesPage;
