import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";
import Sidebar from "../components/Sidebar";
import { useNavigate } from "react-router-dom";

const MarketPage = () => {
  const { user, token } = useAuth();
  const [tab, setTab] = useState("offers");
  const [items, setItems] = useState([]);
  const [categoriasDisponibles, setCategoriasDisponibles] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const navigate = useNavigate();

  // filtros
  const [modalidad, setModalidad] = useState("");
  const [minHoras, setMinHoras] = useState("");
  const [maxHoras, setMaxHoras] = useState("");
  const [q, setQ] = useState("");

  // cargar categorías
  useEffect(() => {
    axios
      .get("http://localhost:8080/categorias")
      .then((res) => setCategoriasDisponibles(res.data))
      .catch((err) => console.error("❌ Error al cargar categorías:", err));
  }, []);

  // cargar items
  useEffect(() => {
    const fetchData = async () => {
      try {
        if (tab === "products") {
          const res = await axios.get("http://localhost:8080/productos");
          setItems(res.data);
        } else {
          const params = {
            tipo: tab === "offers" ? "OFERTA" : "PETICION",
            modalidad: modalidad || undefined,
            minHoras: minHoras || undefined,
            maxHoras: maxHoras || undefined,
            q: q || undefined,
            categorias:
              categorias.length > 0 ? categorias.join(",") : undefined,
          };
          const res = await axios.get(
            "http://localhost:8080/intercambios/filtrar",
            { params }
          );
          setItems(res.data);
        }
      } catch (err) {
        console.error("❌ Error cargando items:", err);
      }
    };
    fetchData();
  }, [tab, modalidad, minHoras, maxHoras, q, categorias]);

  const toggleCategoria = (id) => {
    setCategorias((prev) =>
      prev.includes(id) ? prev.filter((c) => c !== id) : [...prev, id]
    );
  };

  return (
    <div style={styles.layout}>
      <Sidebar />

      {/* Contenido principal */}
      <main style={styles.main}>
        <h2 style={{ marginBottom: "20px" }}>Mercado</h2>

        {/* Tabs */}
        <div style={styles.tabs}>
          {["offers", "requests", "products"].map((t) => (
            <button
              key={t}
              style={tab === t ? styles.activeTab : styles.tab}
              onClick={() => setTab(t)}
            >
              {t === "offers"
                ? "Offers"
                : t === "requests"
                ? "Requests"
                : "Products"}
            </button>
          ))}
        </div>

        {/* Lista */}
        <div style={styles.list}>
          {items.length > 0 ? (
            items.map((i) => (
              <div key={i.id} style={styles.card}>
                {/* Header con nombre + usuario */}
                <div style={styles.cardHeader}>
                  <h3 style={{ margin: 0 }}>{i.nombre}</h3>
                  <div style={styles.userBox}>
                    <img
                      src={
                        tab === "products"
                          ? i.usuarioFoto || "https://via.placeholder.com/32"
                          : i.user?.fotoPerfil ||
                            "https://via.placeholder.com/32"
                      }
                      alt="user"
                      style={styles.avatar}
                    />
                    <span style={styles.username}>
                      {tab === "products"
                        ? i.usuarioNombre || i.usuarioCorreo || "Usuario"
                        : i.user?.nombre || i.user?.correo || "Usuario"}
                    </span>
                  </div>
                </div>

                {/* Meta info */}
                <div style={styles.metaInfo}>
                  {"numeroHoras" in i && (
                    <span style={styles.hours}>{i.numeroHoras}h</span>
                  )}
                  {tab !== "products" && i.modalidad && (
                    <span style={styles.modalidad}>{i.modalidad}</span>
                  )}
                  {tab !== "products" &&
                    i.promedioResenas !== undefined && (
                      <span style={styles.rating}>
                        ⭐ {i.promedioResenas.toFixed(1)}
                      </span>
                    )}
                </div>

                {/* Descripción */}
                <p style={styles.cardText}>
                  {i.descripcion || "Sin descripción"}
                </p>

                {/* Acciones */}
                <div style={styles.actions}>
                  <button
                    style={styles.viewBtn}
                    onClick={() =>
                      tab === "products"
                        ? navigate(`/producto/${i.id}`)
                        : navigate(`/intercambio/${i.id}`)
                    }
                  >
                    View
                  </button>
                </div>
              </div>
            ))
          ) : (
            <p>No hay resultados</p>
          )}
        </div>
      </main>

      {/* Panel de filtros */}
      <aside style={styles.filters}>
        {tab !== "products" && (
          <>
            <h3>Filtros</h3>
            <input
              type="text"
              placeholder="Search the market..."
              value={q}
              onChange={(e) => setQ(e.target.value)}
              style={styles.input}
            />
            <select
              value={modalidad}
              onChange={(e) => setModalidad(e.target.value)}
              style={styles.input}
            >
              <option value="">Todas las modalidades</option>
              <option value="VIRTUAL">Online</option>
              <option value="PRESENCIAL">Presencial</option>
            </select>
            <input
              type="number"
              placeholder="Min hours"
              value={minHoras}
              onChange={(e) => setMinHoras(e.target.value)}
              style={styles.input}
            />
            <input
              type="number"
              placeholder="Max hours"
              value={maxHoras}
              onChange={(e) => setMaxHoras(e.target.value)}
              style={styles.input}
            />

            <div style={{ marginTop: "15px" }}>
              <strong>Categories:</strong>
              <div
                style={{
                  display: "flex",
                  flexWrap: "wrap",
                  gap: "6px",
                  marginTop: "6px",
                }}
              >
                {categoriasDisponibles.map((c) => (
                  <button
                    key={c.id}
                    onClick={() => toggleCategoria(c.id)}
                    style={{
                      padding: "5px 10px",
                      borderRadius: "20px",
                      border: categorias.includes(c.id)
                        ? "2px solid #ff6f00"
                        : "1px solid #ccc",
                      backgroundColor: categorias.includes(c.id)
                        ? "#ff6f00"
                        : "#fff",
                      color: categorias.includes(c.id) ? "#fff" : "#000",
                      cursor: "pointer",
                      fontSize: "12px",
                    }}
                  >
                    {c.nombre}
                  </button>
                ))}
              </div>
            </div>

            <button style={styles.applyBtn}>Aplicar</button>
          </>
        )}
      </aside>
    </div>
  );
};

const styles = {
  layout: { display: "flex", minHeight: "100vh", backgroundColor: "#f9f9f9" },
  main: { flex: 1, padding: "30px", fontFamily: "Arial, sans-serif" },
  tabs: { display: "flex", gap: "10px", marginBottom: "20px" },
  tab: {
    padding: "10px 15px",
    border: "none",
    borderRadius: "6px",
    background: "#eee",
    cursor: "pointer",
  },
  activeTab: {
    padding: "10px 15px",
    border: "none",
    borderRadius: "6px",
    background: "#ff6f00",
    color: "#fff",
    fontWeight: "bold",
  },
  list: { display: "grid", gap: "15px" },
  card: {
    background: "#fff",
    padding: "15px",
    borderRadius: "10px",
    boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
  },
  cardHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "8px",
  },
  userBox: { display: "flex", alignItems: "center", gap: "6px" },
  avatar: {
    width: "32px",
    height: "32px",
    borderRadius: "50%",
    objectFit: "cover",
  },
  username: { fontSize: "14px", fontWeight: "500", color: "#333" },
  metaInfo: {
    display: "flex",
    gap: "10px",
    margin: "6px 0",
    fontSize: "13px",
    color: "#555",
  },
  hours: {
    background: "#f5f5f5",
    padding: "2px 8px",
    borderRadius: "6px",
    fontSize: "12px",
    fontWeight: "bold",
  },
  modalidad: {
    background: "#e9ecef",
    padding: "2px 8px",
    borderRadius: "6px",
    fontSize: "12px",
    fontWeight: "500",
  },
  rating: {
    background: "#fffbea",
    padding: "2px 8px",
    borderRadius: "6px",
    fontSize: "12px",
  },
  cardText: { fontSize: "14px", color: "#555", margin: "6px 0" },
  actions: { display: "flex", gap: "10px", marginTop: "10px" },
  viewBtn: {
    padding: "6px 12px",
    border: "1px solid #ddd",
    background: "#fff",
    borderRadius: "6px",
    cursor: "pointer",
  },
  requestBtn: {
    padding: "6px 12px",
    border: "none",
    background: "#ff6f00",
    color: "#fff",
    borderRadius: "6px",
    cursor: "pointer",
  },
  filters: {
    width: "280px",
    background: "#fff",
    borderLeft: "1px solid #ddd",
    padding: "20px",
  },
  input: {
    width: "100%",
    padding: "8px",
    border: "1px solid #ccc",
    borderRadius: "6px",
    marginBottom: "10px",
  },
  applyBtn: {
    width: "100%",
    padding: "10px",
    border: "none",
    borderRadius: "6px",
    background: "#ff6f00",
    color: "#fff",
    fontWeight: "bold",
    cursor: "pointer",
  },
};

export default MarketPage;
