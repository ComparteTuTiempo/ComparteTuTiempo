import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";

const UserProfile = () => {
  const navigate = useNavigate();
  const { correo } = useParams();
  const { user, token } = useAuth();

  const [usuario, setUsuario] = useState(null);
  const [ofertas, setOfertas] = useState([]);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({
    biografia: "",
    fechaNacimiento: "",
    ubicacion: "",
  });

  // 📌 Reportes
  const [showReportForm, setShowReportForm] = useState(false);
  const [reportData, setReportData] = useState({ titulo: "", descripcion: "" });

  // 📌 Reseñas
  const [resenas, setResenas] = useState([]);
  const [promedio, setPromedio] = useState(0);
  const [nuevaResena, setNuevaResena] = useState({ puntuacion: 5, comentario: "" });

  useEffect(() => {
    const fetchUsuario = async () => {
      if (token) {
        try {
          const targetCorreo = correo || user?.correo;

          const res = await axios.get(
            `http://localhost:8080/api/usuarios/${targetCorreo}`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          setUsuario(res.data);
          setFormData({
            biografia: res.data.biografia || "",
            fechaNacimiento: res.data.fechaNacimiento || "",
            ubicacion: res.data.ubicacion || "",
          });

          if (!correo && user?.correo) {
            const resOfertas = await axios.get(
              `http://localhost:8080/intercambios/usuario/${user.correo}`,
              { headers: { Authorization: `Bearer ${token}` } }
            );
            setOfertas(resOfertas.data);
          }

          // 👇 cargar reseñas y promedio
          const resResenas = await axios.get(
            `http://localhost:8080/api/resenas/${targetCorreo}`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          setResenas(resResenas.data);

          const resProm = await axios.get(
            `http://localhost:8080/api/resenas/${targetCorreo}/promedio`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          setPromedio(resProm.data);
        } catch (err) {
          console.error("❌ Error al cargar el usuario:", err);
        }
      }
    };
    fetchUsuario();
  }, [correo, token, user]);

  const handleChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSave = async () => {
    try {
      const res = await axios.put(
        `http://localhost:8080/api/usuarios/${user.correo}`,
        { ...usuario, ...formData },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setUsuario(res.data);
      setEditing(false);
    } catch (err) {
      console.error("❌ Error al actualizar usuario:", err);
    }
  };

  // 📌 enviar reporte
  const handleReportSubmit = async () => {
    try {
      await axios.post(
        `http://localhost:8080/api/reportes/${usuario.correo}`,
        reportData,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      alert("✅ Reporte enviado con éxito");
      setShowReportForm(false);
      setReportData({ titulo: "", descripcion: "" });
    } catch (err) {
      console.error("❌ Error al enviar reporte:", err);
      alert("Hubo un error al enviar el reporte");
    }
  };

  // 📌 enviar reseña
  const handleResenaSubmit = async () => {
    try {
      await axios.post(
        `http://localhost:8080/api/resenas/${user.correo}/${usuario.correo}`,
        null,
        {
          params: {
            puntuacion: nuevaResena.puntuacion,
            comentario: nuevaResena.comentario,
          },
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      alert("✅ Reseña enviada");
      setNuevaResena({ puntuacion: 5, comentario: "" });

      // recargar reseñas
      const resResenas = await axios.get(
        `http://localhost:8080/api/resenas/${usuario.correo}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setResenas(resResenas.data);

      const resProm = await axios.get(
        `http://localhost:8080/api/resenas/${usuario.correo}/promedio`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setPromedio(resProm.data);
    } catch (err) {
      console.error("❌ Error al enviar reseña:", err);
      alert("Ya ha enviado una reseña para este usuario");
    }
  };

  if (!usuario) {
    return <p style={{ textAlign: "center", marginTop: "40px" }}>Cargando perfil...</p>;
  }

  const isOwnProfile = !correo || correo === user?.correo;

  return (
    <div style={styles.container}>
      <aside style={styles.sidebar}>
        <ul style={styles.menu}>
          <li style={styles.menuItem}>Inicio</li>
          <li style={styles.menuItem}>Mensajes</li>
          <li style={styles.menuItem}>Configuración</li>
        </ul>
      </aside>

      <main style={styles.main}>
        <div style={styles.profileCard}>
          <img
            src={usuario.fotoPerfil || "https://via.placeholder.com/80"}
            alt="Foto de perfil"
            style={styles.avatar}
          />
          <div style={styles.profileInfo}>
            <h2>{usuario.nombre}</h2>
            {usuario.verificado && <p style={styles.verificado}>✔ Usuario verificado</p>}
            {!usuario.activo && <p style={styles.baneado}>🚫 Usuario baneado</p>}
            <p style={styles.email}>{usuario.correo}</p>

            {editing ? (
              <>
                <textarea
                  name="biografia"
                  value={formData.biografia}
                  onChange={handleChange}
                  style={styles.textarea}
                  placeholder="Escribe tu biografía..."
                />
                <input
                  type="date"
                  name="fechaNacimiento"
                  value={formData.fechaNacimiento}
                  onChange={handleChange}
                  style={styles.input}
                />
                <input
                  type="text"
                  name="ubicacion"
                  value={formData.ubicacion}
                  onChange={handleChange}
                  style={styles.input}
                  placeholder="Ubicación"
                />
                <button style={styles.saveBtn} onClick={handleSave}>Guardar</button>
              </>
            ) : (
              <>
                <p style={styles.bio}>{usuario.biografia || "Sin biografía aún."}</p>
                <p style={styles.detail}>
                  <strong>Fecha de nacimiento:</strong>{" "}
                  {usuario.fechaNacimiento || "No especificada"}
                </p>
                <p style={styles.detail}>
                  <strong>Ubicación:</strong>{" "}
                  {usuario.ubicacion || "No especificada"}
                </p>

                {isOwnProfile ? (
                  <button style={styles.editBtn} onClick={() => setEditing(true)}>
                    Editar perfil
                  </button>
                ) : (
                  <button style={styles.reportBtn} onClick={() => setShowReportForm(true)}>
                    Reportar usuario
                  </button>
                )}
              </>
            )}
          </div>
        </div>

        {/* Ofertas */}
        {isOwnProfile && (
          <section style={styles.section}>
            <h3 style={styles.sectionTitle}>Mis Ofertas</h3>
            <div style={styles.cards}>
              {ofertas.length > 0 ? (
                ofertas.map((oferta) => (
                  <div key={oferta.id} style={styles.card}>
                    <h4>{oferta.nombre}</h4>
                    <p>{oferta.descripcion}</p>
                    <p>{oferta.tipo} - {oferta.numeroHoras}h</p>
                  </div>
                ))
              ) : (
                <p>No hay ofertas aún</p>
              )}
            </div>
          </section>
        )}

        {/* Reseñas */}
        <section style={styles.section}>
          <h3 style={styles.sectionTitle}>
            Reseñas recibidas ({promedio.toFixed(1)} ⭐)
          </h3>
          <div style={styles.cards}>
            {resenas.length > 0 ? (
              resenas.map((r) => (
                <div key={r.id} style={styles.card}>
                  <strong>{r.autor?.nombre || "Anónimo"}</strong>
                  <p>{"⭐".repeat(r.puntuacion)}</p>
                  <p>{r.comentario}</p>
                  <small>{new Date(r.fecha).toLocaleDateString()}</small>
                </div>
              ))
            ) : (
              <p>Aún no hay reseñas</p>
            )}
          </div>

          {!isOwnProfile && usuario.activo && (
            <div style={styles.card}>
              <h4>Deja una reseña</h4>
              <select
                value={nuevaResena.puntuacion}
                onChange={(e) =>
                  setNuevaResena({ ...nuevaResena, puntuacion: Number(e.target.value) })
                }
                style={styles.input}
              >
                {[1, 2, 3, 4, 5].map((n) => (
                  <option key={n} value={n}>{n} ⭐</option>
                ))}
              </select>
              <textarea
                placeholder="Escribe un comentario..."
                value={nuevaResena.comentario}
                onChange={(e) => setNuevaResena({ ...nuevaResena, comentario: e.target.value })}
                style={styles.textarea}
              />
              <button style={styles.saveBtn} onClick={handleResenaSubmit}>
                Enviar reseña
              </button>
            </div>
          )}
        </section>
      </main>

      {/* Modal Reporte */}
      {showReportForm && (
        <div style={styles.modal}>
          <div style={styles.modalContent}>
            <h3>Reportar a {usuario.nombre}</h3>
            <input
              type="text"
              placeholder="Título del reporte"
              value={reportData.titulo}
              onChange={(e) => setReportData({ ...reportData, titulo: e.target.value })}
              style={styles.input}
            />
            <textarea
              placeholder="Describe el problema..."
              value={reportData.descripcion}
              onChange={(e) => setReportData({ ...reportData, descripcion: e.target.value })}
              style={styles.textarea}
            />
            <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
              <button style={styles.saveBtn} onClick={handleReportSubmit}>Enviar</button>
              <button style={styles.closeBtn} onClick={() => setShowReportForm(false)}>Cancelar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

const styles = {
  container: {
    display: "flex",
    minHeight: "100vh",
    backgroundColor: "#f8f9fa",
    fontFamily: "Arial, sans-serif",
  },
  sidebar: {
    width: "220px",
    backgroundColor: "#fff",
    borderRight: "1px solid #ddd",
    padding: "20px",
  },
  menu: { listStyle: "none", padding: 0, margin: 0 },
  menuItem: { padding: "10px 0", cursor: "pointer", color: "#333" },
  active: { fontWeight: "bold", color: "#007bff" },
  main: { flex: 1, padding: "30px" },
  profileCard: {
    display: "flex",
    alignItems: "center",
    backgroundColor: "#fff",
    borderRadius: "12px",
    padding: "20px",
    marginBottom: "30px",
    boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
  },
  avatar: {
    width: "80px",
    height: "80px",
    borderRadius: "50%",
    marginRight: "20px",
    objectFit: "cover",
  },
  profileInfo: { flex: 1 },
  email: { color: "#666", margin: "4px 0" },
  bio: { color: "#444", fontSize: "14px", marginBottom: "8px" },
  detail: { fontSize: "13px", color: "#555", marginBottom: "4px" },
  textarea: {
    width: "100%",
    minHeight: "60px",
    padding: "8px",
    borderRadius: "6px",
    border: "1px solid #ccc",
    fontSize: "14px",
    marginBottom: "10px",
  },
  input: {
    width: "100%",
    padding: "8px",
    borderRadius: "6px",
    border: "1px solid #ccc",
    fontSize: "14px",
    marginBottom: "10px",
  },
  editBtn: {
    backgroundColor: "#007bff",
    border: "none",
    color: "#fff",
    padding: "10px 16px",
    borderRadius: "6px",
    cursor: "pointer",
  },
  saveBtn: {
    backgroundColor: "#28a745",
    border: "none",
    color: "#fff",
    padding: "5px 16px",
    borderRadius: "6px",
    cursor: "pointer",
  },
  reportBtn: {
    backgroundColor: "#dc3545",
    border: "none",
    color: "#fff",
    padding: "8px 12px",
    borderRadius: "6px",
    cursor: "pointer",
  },
  section: { marginBottom: "30px" },
  sectionTitle: { marginBottom: "15px" },
  cards: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))",
    gap: "20px",
  },
  card: {
    backgroundColor: "#fff",
    padding: "15px",
    borderRadius: "8px",
    boxShadow: "0 2px 4px rgba(0,0,0,0.1)",
  },
  detailsBtn: {
    marginTop: "10px",
    backgroundColor: "transparent",
    border: "1px solid #007bff",
    color: "#007bff",
    padding: "6px 12px",
    borderRadius: "6px",
    cursor: "pointer",
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
    zIndex: 1000,
  },
  modalContent: {
    backgroundColor: "#fff",
    padding: "20px",
    borderRadius: "8px",
    width: "400px",
    maxWidth: "90%",
    boxShadow: "0 4px 12px rgba(0,0,0,0.2)",
  },
  closeBtn: {
    backgroundColor: "#6c757d",
    border: "none",
    color: "#fff",
    padding: "8px 12px",
    borderRadius: "6px",
    cursor: "pointer",
  },
  verificado: {
    fontSize: "12px",
    color: "#28a745",
    margin: "4px 0",
  },
};


export default UserProfile;
