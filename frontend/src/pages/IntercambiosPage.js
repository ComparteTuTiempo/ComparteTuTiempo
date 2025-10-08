import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";

const IntercambiosPage = () => {
  const { user, token } = useAuth();
  const [intercambios, setIntercambios] = useState([]);
  const [seleccionado, setSeleccionado] = useState(null);
  const [tabActiva, setTabActiva] = useState("OFERTA");

  // filtros
  const [modalidad, setModalidad] = useState("");
  const [minHoras, setMinHoras] = useState("");
  const [maxHoras, setMaxHoras] = useState("");
  const [q, setQ] = useState("");
  const [categorias, setCategorias] = useState([]);
  const [categoriasDisponibles, setCategoriasDisponibles] = useState([]);

  // reseñas
  const [resenas, setResenas] = useState([]);
  const [promedio, setPromedio] = useState(0);
  const [nuevaResena, setNuevaResena] = useState({ puntuacion: 5, comentario: "" });

  // cargar categorías al inicio
  useEffect(() => {
    axios.get(`http://localhost:8080/categorias`)
      .then(res => setCategoriasDisponibles(res.data))
      .catch(err => console.error("❌ Error al cargar categorías:", err));
  }, []);

  // cargar intercambios con filtros
  useEffect(() => {
    const obtenerIntercambios = async () => {
      try {
        const params = {
          tipo: tabActiva,
          modalidad: modalidad || undefined,
          minHoras: minHoras || undefined,
          maxHoras: maxHoras || undefined,
          q: q || undefined,
          categorias: categorias.length > 0 ? categorias.join(",") : undefined,
        };

        const response = await axios.get(`http://localhost:8080/intercambios/filtrar`, { params });
        setIntercambios(response.data);
      } catch (error) {
        console.error("❌ Error al obtener intercambios:", error);
      }
    };

    obtenerIntercambios();
  }, [tabActiva, modalidad, minHoras, maxHoras, q, categorias]);

  // manejar selección/deselección de categorías
  const toggleCategoria = (id) => {
    setCategorias((prev) =>
      prev.includes(id) ? prev.filter((c) => c !== id) : [...prev, id]
    );
  };

  // eliminar intercambio (admin o dueño)
  const eliminarIntercambio = async (id) => {
    if (!window.confirm("¿Seguro que deseas eliminar este intercambio?")) return;

    try {
      await axios.delete(`http://localhost:8080/intercambios/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setIntercambios((prev) => prev.filter((i) => i.id !== id));
      setSeleccionado(null);
      alert("✅ Intercambio eliminado correctamente");
    } catch (err) {
      console.error("❌ Error al eliminar intercambio:", err);
      alert("No se pudo eliminar el intercambio");
    }
  };

  // cargar reseñas cuando se selecciona un intercambio
  useEffect(() => {
    if (seleccionado) {
      axios.get(`http://localhost:8080/resenas/intercambios/${seleccionado.id}`)
        .then(res => setResenas(res.data))
        .catch(err => console.error("❌ Error al cargar reseñas:", err));

      axios.get(`http://localhost:8080/resenas/intercambios/${seleccionado.id}/promedio`)
        .then(res => setPromedio(res.data))
        .catch(err => console.error("❌ Error al cargar promedio:", err));
    }
  }, [seleccionado]);

  // comprobar si el usuario ya dejó reseña
  const yaHaResenado = user && resenas.some(r => r.autor?.correo === user.correo);

  // enviar reseña
  const enviarResena = async () => {
    if (!nuevaResena.comentario.trim()) {
      alert("⚠ El comentario no puede estar vacío");
      return;
    }

    try {
      await axios.post(
        `http://localhost:8080/resenas/intercambios/${seleccionado.id}`,
        nuevaResena,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      alert("✅ Reseña enviada con éxito");

      // refrescar reseñas
      const res = await axios.get(`http://localhost:8080/resenas/intercambios/${seleccionado.id}`);
      setResenas(res.data);

      const avg = await axios.get(`http://localhost:8080/resenas/intercambios/${seleccionado.id}/promedio`);
      setPromedio(avg.data);

      setNuevaResena({ puntuacion: 5, comentario: "" });
    } catch (err) {
      console.error("❌ Error al enviar reseña:", err);
      alert("No se pudo enviar la reseña");
    }
  };

  return (
    <div style={{ padding: "20px" }}>
      <h1>Intercambios</h1>

      {/* Pestañas */}
      <div style={{ display: "flex", marginBottom: "20px" }}>
        {["OFERTA", "PETICION"].map((tipo) => (
          <button
            key={tipo}
            onClick={() => setTabActiva(tipo)}
            style={{
              flex: 1,
              padding: "10px",
              border: "none",
              cursor: "pointer",
              backgroundColor: tabActiva === tipo ? "#007bff" : "#f1f1f1",
              color: tabActiva === tipo ? "white" : "black",
              fontWeight: tabActiva === tipo ? "bold" : "normal",
              borderRadius: "5px 5px 0 0",
            }}
          >
            {tipo}
          </button>
        ))}
      </div>

      {/* Filtros */}
      <div style={{
        marginBottom: "20px",
        padding: "15px",
        border: "1px solid #ddd",
        borderRadius: "8px",
        backgroundColor: "#fafafa"
      }}>
        <h3>Filtros</h3>
        <input
          type="text"
          placeholder="Buscar por texto..."
          value={q}
          onChange={(e) => setQ(e.target.value)}
          style={{ marginRight: "10px", padding: "5px" }}
        />
        <select
          value={modalidad}
          onChange={(e) => setModalidad(e.target.value)}
          style={{ marginRight: "10px", padding: "5px" }}
        >
          <option value="">Todas las modalidades</option>
          <option value="VIRTUAL">Online</option>
          <option value="PRESENCIAL">Presencial</option>
        </select>
        <input
          type="number"
          placeholder="Mín horas"
          value={minHoras}
          onChange={(e) => setMinHoras(e.target.value)}
          style={{ width: "100px", marginRight: "10px", padding: "5px" }}
        />
        <input
          type="number"
          placeholder="Máx horas"
          value={maxHoras}
          onChange={(e) => setMaxHoras(e.target.value)}
          style={{ width: "100px", marginRight: "10px", padding: "5px" }}
        />

        {/* Categorías */}
        <div style={{ marginTop: "10px" }}>
          <strong>Categorías:</strong>
          <div style={{ display: "flex", flexWrap: "wrap", gap: "10px", marginTop: "8px" }}>
            {categoriasDisponibles.map((c) => (
              <button
                key={c.id}
                onClick={() => toggleCategoria(c.id)}
                style={{
                  padding: "5px 10px",
                  borderRadius: "5px",
                  border: categorias.includes(c.id) ? "2px solid #007bff" : "1px solid #ccc",
                  backgroundColor: categorias.includes(c.id) ? "#007bff" : "white",
                  color: categorias.includes(c.id) ? "white" : "black",
                  cursor: "pointer"
                }}
              >
                {c.nombre}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Lista */}
      <div style={{
        display: "grid",
        gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))",
        gap: "20px",
        marginTop: "10px",
      }}>
        {intercambios.map((i) => (
          <div
            key={i.id}
            onClick={() => setSeleccionado(i)}
            style={{
              border: "1px solid #ccc",
              borderRadius: "8px",
              padding: "15px",
              cursor: "pointer",
              boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
            }}
          >
            <h3>{i.nombre}</h3>
            <p>{i.descripcion}</p>
            <p><strong>{i.numeroHoras}</strong> horas</p>
            <small>{i.modalidad}</small>
          </div>
        ))}
      </div>

      {/* Modal de detalle */}
      {seleccionado && (
        <div style={{
          position: "fixed", top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: "rgba(0,0,0,0.5)", display: "flex",
          justifyContent: "center", alignItems: "center", zIndex: 1000
        }}>
          <div style={{
            background: "#fff", padding: "30px", borderRadius: "10px", width: "500px",
            position: "relative"
          }}>
            <button
              onClick={() => setSeleccionado(null)}
              style={{
                position: "absolute", top: "10px", right: "10px",
                border: "none", background: "none", fontSize: "18px", cursor: "pointer"
              }}
            >
              ✖
            </button>
            <h2>{seleccionado.nombre}</h2>
            <p><strong>Descripción:</strong> {seleccionado.descripcion}</p>
            <p><strong>Horas:</strong> {seleccionado.numeroHoras}</p>
            <p><strong>Tipo:</strong> {seleccionado.tipo}</p>
            <p><strong>Modalidad:</strong> {seleccionado.modalidad}</p>
            <p><strong>Categorías:</strong> {seleccionado.categorias?.map(c => c.nombre).join(", ") || "Ninguna"}</p>

            {/* 👇 Mostrar calificación y reseñas solo si es OFERTA */}
            {seleccionado.tipo === "OFERTA" && (
              <>
                <p><strong>⭐ Promedio reseñas:</strong> {promedio.toFixed(1)} / 5</p>

                {/* Listado de reseñas */}
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

                {/* Formulario para nueva reseña */}
                {user?.correo !== seleccionado.user?.correo && !yaHaResenado && (
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

            {/* Botón de eliminar si es admin */}
            {user?.roles?.includes("ADMIN") && (
              <button
                onClick={() => eliminarIntercambio(seleccionado.id)}
                style={{
                  marginTop: "15px",
                  backgroundColor: "#dc3545",
                  color: "white",
                  border: "none",
                  padding: "10px 15px",
                  borderRadius: "6px",
                  cursor: "pointer"
                }}
              >
                Eliminar (Admin)
              </button>
            )}
          </div>
        </div>
      )}

    </div>
  );
};

export default IntercambiosPage;
