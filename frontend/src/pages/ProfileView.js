import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";



const UserProfile = () => {
  const { user, token } = useAuth();
  const [usuario, setUsuario] = useState(null);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({
    biografia: "",
    fechaNacimiento: "",
    ubicacion: "",
  });

  useEffect(() => {
    const fetchUsuario = async () => {
      if (user?.correo && token) {
        try {
          const res = await axios.get(
            `http://localhost:8080/api/usuarios/${user.correo}`,
            {
              headers: { Authorization: `Bearer ${token}` },
            }
          );
          setUsuario(res.data);
          setFormData({
            biografia: res.data.biografia || "",
            fechaNacimiento: res.data.fechaNacimiento || "",
            ubicacion: res.data.ubicacion || "",
          });
        } catch (err) {
          console.error("❌ Error al cargar el usuario:", err);
        }
      }
    };
    fetchUsuario();
  }, [user, token]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSave = async () => {
    try {
      const res = await axios.put(
        `http://localhost:8080/api/usuarios/${user.correo}`,
        { ...usuario, ...formData },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      const updated = res.data;
      setUsuario(updated);
      setEditing(false);
      setFormData({
            biografia: updated.biografia || "",
            fechaNacimiento: updated.fechaNacimiento || "",
            ubicacion: updated.ubicacion || "",
          });
    } catch (err) {
      console.error("❌ Error al actualizar usuario:", err);
    }
  };

  if (!usuario) {
    return <p style={{ textAlign: "center", marginTop: "40px" }}>Cargando perfil...</p>;
  }

  return (
    <div style={styles.container}>
      {/* Sidebar */}
      <aside style={styles.sidebar}>
        <ul style={styles.menu}>
          <li style={styles.menuItem}>Browse Offers</li>
          <li style={styles.menuItem}>Create Offer</li>
          <li style={{ ...styles.menuItem, ...styles.active }}>My Profile</li>
          <li style={styles.menuItem}>Chats</li>
          <li style={styles.menuItem}>Reviews</li>
        </ul>
      </aside>

      {/* Main content */}
      <main style={styles.main}>
        {/* Profile card */}
        <div style={styles.profileCard}>
          <img
            src={usuario.fotoPerfil || "https://via.placeholder.com/80"}
            alt="Foto de perfil"
            style={styles.avatar}
          />
          <div style={styles.profileInfo}>
            <h2>{usuario.nombre}</h2>
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
              </>
            )}
          </div>

          {editing ? (
            <button style={styles.saveBtn} onClick={handleSave}>
              Guardar
            </button>
          ) : (
            <button style={styles.editBtn} onClick={() => setEditing(true)}>
              Edit Profile
            </button>
          )}
        </div>

        {/* Offers */}
        <section style={styles.section}>
          <h3 style={styles.sectionTitle}>My Offers</h3>
          <div style={styles.cards}>
            <div style={styles.card}>
              <h4>Gardening Help</h4>
              <p>Offering help with weeding, planting, and general garden maintenance.</p>
              <button style={styles.detailsBtn}>View Details</button>
            </div>
            <div style={styles.card}>
              <h4>Basic Web Design</h4>
              <p>Can help set up simple websites using HTML/CSS.</p>
              <button style={styles.detailsBtn}>View Details</button>
            </div>
          </div>
        </section>

        {/* Reviews */}
        <section style={styles.section}>
          <h3 style={styles.sectionTitle}>Reviews Received</h3>
          <div style={styles.cards}>
            <div style={styles.card}>
              <strong>Jane Smith</strong>
              <p>John did an amazing job with my garden! Very thorough and knowledgeable.</p>
              <small>2 days ago</small>
            </div>
            <div style={styles.card}>
              <strong>Mike Johnson</strong>
              <p>Helped me with a quick website fix. Professional and efficient.</p>
              <small>1 week ago</small>
            </div>
          </div>
        </section>
      </main>
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
};

export default UserProfile;



