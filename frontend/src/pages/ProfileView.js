import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";
import Sidebar from "../components/Sidebar";

const UserProfile = () => {
  const { correo } = useParams();
  const { user, token } = useAuth(); // 👈 obtenemos user y token del contexto
  const navigate = useNavigate();

  const [usuario, setUsuario] = useState(null);
  const [resenas, setResenas] = useState([]);
  const [promedio, setPromedio] = useState(0);
  const [ofertas, setOfertas] = useState([]);
  const [tab, setTab] = useState("reviews");

  const [showReportForm, setShowReportForm] = useState(false);
  const [reportReason, setReportReason] = useState("");

  const [showEditModal, setShowEditModal] = useState(false);
  const [formData, setFormData] = useState({
    biografia: "",
    ubicacion: "",
    fechaNacimiento: "",
  });

  const [nuevaResena, setNuevaResena] = useState({ puntuacion: 5, comentario: "" });

  // 👇 comprobamos si el logueado es admin desde AuthContext
  const isAdmin = user?.roles?.includes("ADMIN");

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
            ubicacion: res.data.ubicacion || "",
            fechaNacimiento: res.data.fechaNacimiento || "",
            fotoPerfil: res.data.fotoPerfil || "",
          });

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

          const resOfertas = await axios.get(
            `http://localhost:8080/intercambios/usuario/${targetCorreo}`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          setOfertas(resOfertas.data);
        } catch (err) {
          console.error("❌ Error al cargar datos de usuario:", err);
        }
      }
    };
    fetchUsuario();
  }, [correo, token, user]);

  if (!usuario) {
    navigate("/login");
    return null;
  }

  if (!usuario.activo) {
    return (
      <div style={{ textAlign: "center", marginTop: "40px", fontSize: "20px", fontWeight: "bold", color: "#dc3545" }}>
        🚫 Usuario baneado
      </div>
    );
  }

  const isOwnProfile = !correo || correo === user?.correo;

  const handleSaveProfile = async () => {
    try {
      const payload = {
        biografia: formData.biografia,
        ubicacion: formData.ubicacion,
        fechaNacimiento: formData.fechaNacimiento,
        fotoPerfil: formData.fotoPerfil,
      };

      await axios.put(
        `http://localhost:8080/api/usuarios/${user.correo}`,
        payload,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setUsuario((prev) => ({ ...prev, ...payload }));
      setShowEditModal(false);
      alert("✅ Perfil actualizado con éxito");
    } catch (err) {
      console.error("❌ Error al actualizar usuario:", err);
      alert("Hubo un error al actualizar tu perfil");
    }
  };

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
      alert("Ya has enviado una reseña para este usuario");
    }
  };

  const handleStartConversation = async () => {
    try {
      const res = await axios.post(
        `http://localhost:8080/conversaciones?correos=${user.correo}&correos=${usuario.correo}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      navigate(`/conversaciones/${res.data.id}`);
    } catch (err) {
      console.error("❌ Error al iniciar conversación:", err);
      alert("No se pudo iniciar la conversación");
    }
  };

  const handleReport = async () => {
    if (!reportReason.trim()) {
      alert("Escribe un motivo antes de enviar el reporte");
      return;
    }
    try {
      await axios.post(
        `http://localhost:8080/api/reportes/${usuario.correo}`,
        { titulo: "Reporte de usuario", descripcion: reportReason },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      alert("✅ Reporte enviado, un administrador lo revisará.");
      setReportReason("");
      setShowReportForm(false);
    } catch (err) {
      console.error("❌ Error al enviar reporte:", err);
      alert("Hubo un error al enviar el reporte");
    }
  };

  return (
    <div style={styles.container}>
      <Sidebar />

      <main style={styles.main}>
        {/* Tarjeta de perfil */}
        <div style={styles.profileCard}>
          <img
            src={usuario.fotoPerfil || "https://via.placeholder.com/80"}
            alt="Foto de perfil"
            style={styles.avatar}
          />
          <div>
            <h2 style={styles.name}>{usuario.nombre}</h2>
            {usuario.verificado && <p style={styles.verifiedText}>✅ Cuenta verificada</p>}
            <p style={styles.bio}>{usuario.biografia || "Sin biografía aún. Agrega algo sobre ti."}</p>
            <p style={styles.location}>📍 {usuario.ubicacion || "Ubicación no especificada"}</p>
          </div>
        </div>

        {/* Stats */}
        <div style={styles.stats}>
          <div style={styles.statBox}>
            <h3>Reputation</h3>
            <p>{promedio.toFixed(1)} ⭐</p>
          </div>
        </div>

        {/* Botones extra */}
        {!isOwnProfile && (
          <div style={{ marginBottom: "20px", display: "flex", gap: "10px" }}>
            <button style={styles.chatBtn} onClick={handleStartConversation}>
              Iniciar conversación
            </button>
            <button style={styles.reportBtn} onClick={() => setShowReportForm(!showReportForm)}>
              Reportar usuario
            </button>
          </div>
        )}

        {showReportForm && (
          <div style={styles.card}>
            <h4>Reportar usuario</h4>
            <textarea
              placeholder="Describe el motivo del reporte..."
              value={reportReason}
              onChange={(e) => setReportReason(e.target.value)}
              style={styles.textarea}
            />
            <div style={{ display: "flex", gap: "10px" }}>
              <button style={styles.saveBtn} onClick={handleReport}>
                Enviar reporte
              </button>
              <button
                style={styles.closeBtn}
                onClick={() => {
                  setShowReportForm(false);
                  setReportReason("");
                }}
              >
                Cancelar
              </button>
            </div>
          </div>
        )}

        {/* Botones de perfil propio */}
        {isOwnProfile && (
          <div style={{ marginBottom: "20px", display: "flex", gap: "10px" }}>
            <button style={styles.editBtn} onClick={() => setShowEditModal(true)}>
              Editar perfil
            </button>
            {!usuario.verificado && !isAdmin && (
              <button style={styles.verifyBtn} onClick={() => navigate("/verificacion")}>
                🔒 Verificar identidad
              </button>
            )}
          </div>
        )}

        {/* Tabs */}
        <div style={styles.tabs}>
          <button style={tab === "reviews" ? styles.activeTab : styles.tab} onClick={() => setTab("reviews")}>
            Reviews
          </button>
          <button style={tab === "offers" ? styles.activeTab : styles.tab} onClick={() => setTab("offers")}>
            Mis Ofertas
          </button>
        </div>

        {/* Contenido de pestañas */}
        <div style={styles.tabContent}>
          {tab === "reviews" && (
            <div>
              <h3>Reseñas recibidas ({promedio.toFixed(1)} ⭐)</h3>
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
                <p>No hay reseñas aún</p>
              )}
              {!isOwnProfile && usuario.activo && (
                <div style={styles.card}>
                  <h4>Deja una reseña</h4>
                  <select
                    value={nuevaResena.puntuacion}
                    onChange={(e) => setNuevaResena({ ...nuevaResena, puntuacion: Number(e.target.value) })}
                    style={styles.input}
                  >
                    {[1, 2, 3, 4, 5].map((n) => (
                      <option key={n} value={n}>
                        {n} ⭐
                      </option>
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
            </div>
          )}

          {tab === "offers" && (
            <div>
              <h3>Resumen de mis ofertas</h3>
              {ofertas.length > 0 ? (
                ofertas.map((o) => (
                  <div key={o.id} style={styles.card}>
                    <h4>{o.nombre}</h4>
                    <p>{o.descripcion}</p>
                    <p>
                      {o.tipo} – {o.numeroHoras}h
                    </p>
                    <small>
                      Reseñas: {o.promedioResenas ? `${o.promedioResenas.toFixed(1)} ⭐` : "Sin reseñas"}
                    </small>
                  </div>
                ))
              ) : (
                <p>No has publicado ofertas aún</p>
              )}
            </div>
          )}
        </div>
      </main>

      {showEditModal && (
        <div style={styles.modal}>
          <div style={styles.modalContent}>
            <h3>Edit Profile</h3>
            <textarea
              name="biografia"
              value={formData.biografia}
              onChange={(e) => setFormData({ ...formData, biografia: e.target.value })}
              placeholder="Escribe tu biografía..."
              style={styles.textarea}
            />
            <input
              type="text"
              name="ubicacion"
              value={formData.ubicacion}
              onChange={(e) => setFormData({ ...formData, ubicacion: e.target.value })}
              placeholder="Ubicación"
              style={styles.input}
            />
            <input
              type="date"
              name="fechaNacimiento"
              value={formData.fechaNacimiento}
              onChange={(e) => setFormData({ ...formData, fechaNacimiento: e.target.value })}
              style={styles.input}
            />
            <input
              type="text"
              name="fotoPerfil"
              value={formData.fotoPerfil}
              onChange={(e) => setFormData({ ...formData, fotoPerfil: e.target.value })}
              placeholder="URL de la foto de perfil"
              style={styles.input}
            />
            <div style={{ display: "flex", gap: "10px", marginTop: "15px" }}>
              <button style={styles.saveBtn} onClick={handleSaveProfile}>
                Guardar
              </button>
              <button style={styles.closeBtn} onClick={() => setShowEditModal(false)}>
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

const styles = {
  container: { display: "flex", minHeight: "100vh", backgroundColor: "#f8f9fa" },
  main: { flex: 1, padding: "30px", fontFamily: "Arial, sans-serif" },
  profileCard: {
    display: "flex",
    alignItems: "center",
    backgroundColor: "#fff",
    borderRadius: "12px",
    padding: "20px",
    marginBottom: "30px",
    boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
    gap: "20px",
  },
  avatar: { width: "80px", height: "80px", borderRadius: "50%", objectFit: "cover" },
  name: { fontSize: "22px", fontWeight: "bold", marginBottom: "5px" },
  bio: { fontSize: "14px", color: "#444", marginBottom: "5px" },
  location: { fontSize: "13px", color: "#777" },
  stats: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
    gap: "20px",
    marginBottom: "10px",
  },
  statBox: {
    backgroundColor: "#fff",
    padding: "20px",
    borderRadius: "12px",
    boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
    textAlign: "center",
  },
  editBtn: {
    padding: "12px 20px",
    backgroundColor: "#ff6f00",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    fontWeight: "bold",
    cursor: "pointer",
  },
  verifyBtn: {
    padding: "12px 20px",
    backgroundColor: "#17a2b8", // celeste
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    fontWeight: "bold",
    cursor: "pointer",
    boxShadow: "0 2px 4px rgba(0,0,0,0.2)",
    transition: "background-color 0.2s ease",
  },
  tabs: { display: "flex", gap: "10px", borderBottom: "2px solid #ddd", marginBottom: "20px" },
  tab: {
    padding: "10px 15px",
    border: "none",
    background: "transparent",
    cursor: "pointer",
    fontWeight: "bold",
    color: "#666",
  },
  activeTab: {
    padding: "10px 15px",
    border: "none",
    background: "transparent",
    cursor: "pointer",
    fontWeight: "bold",
    color: "#ff6f00",
    borderBottom: "3px solid #ff6f00",
  },
  tabContent: { marginTop: "20px" },
  card: {
    backgroundColor: "#fff",
    padding: "15px",
    borderRadius: "8px",
    boxShadow: "0 2px 4px rgba(0,0,0,0.1)",
    marginBottom: "15px",
  },
  modal: {
    position: "fixed",
    top: 0, left: 0, right: 0, bottom: 0,
    backgroundColor: "rgba(0,0,0,0.5)",
    display: "flex", justifyContent: "center", alignItems: "center",
    zIndex: 1000,
  },
  modalContent: {
    backgroundColor: "#fff",
    padding: "20px",
    borderRadius: "8px",
    width: "400px",
    maxWidth: "90%",
    boxShadow: "0 2px 10px rgba(0,0,0,0.2)",
  },
  textarea: {
    width: "100%",
    minHeight: "60px",
    padding: "8px",
    borderRadius: "6px",
    border: "1px solid #ccc",
    marginBottom: "10px",
  },
  input: {
    width: "100%",
    padding: "8px",
    borderRadius: "6px",
    border: "1px solid #ccc",
    marginBottom: "10px",
  },
  saveBtn: {
    flex: 1,
    backgroundColor: "#28a745",
    color: "#fff",
    border: "none",
    borderRadius: "6px",
    padding: "10px",
    cursor: "pointer",
  },
  closeBtn: {
    flex: 1,
    backgroundColor: "#6c757d",
    color: "#fff",
    border: "none",
    borderRadius: "6px",
    padding: "10px",
    cursor: "pointer",
  },
  chatBtn: {
    padding: "10px 16px",
    backgroundColor: "#007bff",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontWeight: "bold",
  },
  reportBtn: {
    padding: "10px 16px",
    backgroundColor: "#dc3545",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontWeight: "bold",
  },
  verifiedText: {
    fontSize: "14px",
    fontWeight: "bold",
    color: "#28a745",
    marginTop: "4px",
  },
};

export default UserProfile;
