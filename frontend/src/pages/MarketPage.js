import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";
import Sidebar from "../components/Sidebar";

const MarketPage = () => {
    const { user, token } = useAuth();
    const [tab, setTab] = useState("offers");
    const [items, setItems] = useState([]);
    const [categoriasDisponibles, setCategoriasDisponibles] = useState([]);
    const [categorias, setCategorias] = useState([]);
    const [selected, setSelected] = useState(null);

    // filtros
    const [modalidad, setModalidad] = useState("");
    const [minHoras, setMinHoras] = useState("");
    const [maxHoras, setMaxHoras] = useState("");
    const [q, setQ] = useState("");

    // reseñas
    const [resenas, setResenas] = useState([]);
    const [promedio, setPromedio] = useState(0);
    const [nuevaResena, setNuevaResena] = useState({ puntuacion: 5, comentario: "" });

    // cargar categorías
    useEffect(() => {
        axios.get("http://localhost:8080/categorias")
            .then(res => setCategoriasDisponibles(res.data))
            .catch(err => console.error("❌ Error al cargar categorías:", err));
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
                        categorias: categorias.length > 0 ? categorias.join(",") : undefined,
                    };
                    const res = await axios.get("http://localhost:8080/intercambios/filtrar", { params });
                    setItems(res.data);
                }
            } catch (err) {
                console.error("❌ Error cargando items:", err);
            }
        };
        fetchData();
    }, [tab, modalidad, minHoras, maxHoras, q, categorias]);

    // cargar reseñas cuando se abre un intercambio
    useEffect(() => {
        if (selected && tab !== "products") {
            axios.get(`http://localhost:8080/resenas/intercambios/${selected.id}`)
                .then(res => setResenas(res.data))
                .catch(err => console.error("❌ Error al cargar reseñas:", err));

            axios.get(`http://localhost:8080/resenas/intercambios/${selected.id}/promedio`)
                .then(res => setPromedio(res.data))
                .catch(err => console.error("❌ Error al cargar promedio:", err));
        }
    }, [selected, tab]);

    const toggleCategoria = (id) => {
        setCategorias((prev) =>
            prev.includes(id) ? prev.filter((c) => c !== id) : [...prev, id]
        );
    };

    const yaHaResenado = user && resenas.some(r => r.autor?.correo === user.correo);

    const enviarResena = async () => {
        if (!nuevaResena.comentario.trim()) {
            alert("⚠ El comentario no puede estar vacío");
            return;
        }
        try {
            await axios.post(
                `http://localhost:8080/resenas/intercambios/${selected.id}`,
                nuevaResena,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            alert("✅ Reseña enviada");

            const res = await axios.get(`http://localhost:8080/resenas/intercambios/${selected.id}`);
            setResenas(res.data);
            const avg = await axios.get(`http://localhost:8080/resenas/intercambios/${selected.id}/promedio`);
            setPromedio(avg.data);

            setNuevaResena({ puntuacion: 5, comentario: "" });
        } catch (err) {
            console.error("❌ Error al enviar reseña:", err);
            alert("No se pudo enviar la reseña");
        }
    };

    // eliminar item (admin)
    const eliminarItem = async (id) => {
        if (!window.confirm("¿Seguro que deseas eliminar este item?")) return;
        try {
            if (tab === "products") {
                await axios.delete(`http://localhost:8080/productos/${id}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
            } else {
                await axios.delete(`http://localhost:8080/intercambios/${id}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
            }
            setItems((prev) => prev.filter((i) => i.id !== id));
            setSelected(null);
            alert("✅ Eliminado correctamente");
        } catch (err) {
            console.error("❌ Error al eliminar:", err);
            alert("No se pudo eliminar el item");
        }
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
                            {t === "offers" ? "Offers" : t === "requests" ? "Requests" : "Products"}
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
                                            src={i.user?.fotoPerfil || "https://via.placeholder.com/32"}
                                            alt="user"
                                            style={styles.avatar}
                                        />
                                        <span style={styles.username}>
                                            {i.user?.nombre || i.user?.correo || "Usuario"}
                                        </span>
                                    </div>
                                </div>

                                {/* Solo ofertas/peticiones */}
                                {tab !== "products" && (
                                    <div style={styles.metaInfo}>
                                        {"numeroHoras" in i && (
                                            <span style={styles.hours}>{i.numeroHoras}h</span>
                                        )}
                                        {i.modalidad && (
                                            <span style={styles.modalidad}>{i.modalidad}</span>
                                        )}
                                        {i.promedioResenas !== undefined && (
                                            <span style={styles.rating}>⭐ {i.promedioResenas.toFixed(1)}</span>
                                        )}
                                    </div>
                                )}

                                {/* Products: mostrar descripción */}
                                {tab === "products" && (
                                    <p style={{ margin: "6px 0" }}>{i.descripcion}</p>
                                )}

                                {/* Acciones */}
                                <div style={styles.actions}>
                                    <button style={styles.viewBtn} onClick={() => setSelected(i)}>
                                        View
                                    </button>
                                    {tab !== "products" && (
                                        <button style={styles.requestBtn}>Request Time</button>
                                    )}
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
                            <div style={{ display: "flex", flexWrap: "wrap", gap: "6px", marginTop: "6px" }}>
                                {categoriasDisponibles.map((c) => (
                                    <button
                                        key={c.id}
                                        onClick={() => toggleCategoria(c.id)}
                                        style={{
                                            padding: "5px 10px",
                                            borderRadius: "20px",
                                            border: categorias.includes(c.id) ? "2px solid #ff6f00" : "1px solid #ccc",
                                            backgroundColor: categorias.includes(c.id) ? "#ff6f00" : "#fff",
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

                {tab === "products" && (
                    <>
                    </>
                )}
            </aside>

            {/* Modal detalle */}
            {selected && (
                <div style={styles.modal}>
                    <div style={styles.modalContent}>
                        <button onClick={() => setSelected(null)} style={styles.closeBtn}>✖</button>
                        <h2>{selected.nombre}</h2>
                        <p><strong>Descripción:</strong> {selected.descripcion}</p>
                        {"numeroHoras" in selected && <p><strong>Horas:</strong> {selected.numeroHoras}</p>}
                        {selected.tipo && <p><strong>Tipo:</strong> {selected.tipo}</p>}
                        {selected.modalidad && <p><strong>Modalidad:</strong> {selected.modalidad}</p>}
                        {selected.estado && <p><strong>Estado:</strong> {selected.estado}</p>}
                        {selected.fechaPublicacion && (
                            <p><strong>Publicado:</strong> {new Date(selected.fechaPublicacion).toLocaleDateString()}</p>
                        )}

                        {/* Reseñas (ofertas y peticiones) */}
                        {tab !== "products" && ["OFERTA", "PETICION"].includes(selected?.tipo) && (
                            <>
                                <p><strong>⭐ Promedio reseñas:</strong> {promedio.toFixed(1)} / 5</p>

                                <div style={{ marginTop: "15px" }}>
                                    <h4>Reseñas:</h4>
                                    {resenas.length > 0 ? (
                                        resenas.map((r) => (
                                            <div key={r.id} style={{ borderBottom: "1px solid #ddd", padding: "5px 0" }}>
                                                <strong>{r.autor?.nombre || "Anónimo"}</strong> ⭐ {r.puntuacion}
                                                <p>{r.comentario}</p>
                                            </div>
                                        ))
                                    ) : (
                                        <p>No hay reseñas aún.</p>
                                    )}
                                </div>

                                {user?.correo !== selected?.user?.correo && !yaHaResenado && (
                                    <div style={{ marginTop: "15px" }}>
                                        <h4>Dejar una reseña</h4>
                                        <select
                                            value={nuevaResena.puntuacion}
                                            onChange={(e) => setNuevaResena({ ...nuevaResena, puntuacion: parseInt(e.target.value) })}
                                            style={{ marginRight: "10px", padding: "5px" }}
                                        >
                                            {[1, 2, 3, 4, 5].map((n) => (
                                                <option key={n} value={n}>{n}</option>
                                            ))}
                                        </select>
                                        <textarea
                                            placeholder="Escribe tu comentario..."
                                            value={nuevaResena.comentario}
                                            onChange={(e) => setNuevaResena({ ...nuevaResena, comentario: e.target.value })}
                                            style={{ width: "100%", minHeight: "60px", marginTop: "10px", padding: "5px" }}
                                        />
                                        <button
                                            onClick={enviarResena}
                                            style={{ marginTop: "10px", backgroundColor: "#28a745", color: "white", border: "none", padding: "8px 12px", borderRadius: "6px", cursor: "pointer" }}
                                        >
                                            Enviar reseña
                                        </button>
                                    </div>
                                )}
                            </>
                        )}

                        {user?.roles?.includes("ADMIN") && (
                            <button
                                onClick={() => eliminarItem(selected.id)}
                                style={styles.deleteBtn}
                            >
                                🗑 Eliminar
                            </button>
                        )}
                    </div>
                </div>
            )}
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
    avatar: { width: "32px", height: "32px", borderRadius: "50%", objectFit: "cover" },
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
    tag: {
        background: "#f1f1f1",
        padding: "6px 12px",
        borderRadius: "20px",
        fontSize: "12px",
    },
    modal: {
        position: "fixed", top: 0, left: 0, right: 0, bottom: 0,
        background: "rgba(0,0,0,0.5)",
        display: "flex", justifyContent: "center", alignItems: "center",
        zIndex: 1000,
    },
    modalContent: {
        background: "#fff",
        padding: "20px",
        borderRadius: "10px",
        maxWidth: "500px",
        width: "100%",
        position: "relative",
    },
    closeBtn: {
        position: "absolute", top: "10px", right: "10px",
        border: "none", background: "none", fontSize: "18px", cursor: "pointer",
    },
    deleteBtn: {
        marginTop: "15px",
        padding: "10px 15px",
        border: "none",
        borderRadius: "6px",
        backgroundColor: "#dc3545",
        color: "white",
        cursor: "pointer",
    },
};

export default MarketPage;
