import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";

const ProductoForm = () => {
  const { token } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams(); // Para edición

  const [formData, setFormData] = useState({
    nombre: "",
    descripcion: "",
    numeroHoras: "",
  });

  // Cargar producto existente si hay id
  useEffect(() => {
    if (id) {
      axios
        .get(`http://localhost:8080/productos/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((res) => {
          setFormData({
            nombre: res.data.nombre || "",
            descripcion: res.data.descripcion || "",
            numeroHoras: res.data.numeroHoras || "",
          });
        })
        .catch((err) => console.error("❌ Error al cargar producto:", err));
    }
  }, [id, token]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (id) {
        // Actualizar
        await axios.put(`http://localhost:8080/productos/${id}`, formData, {
          headers: { Authorization: `Bearer ${token}` },
        });
        alert("Producto actualizado ✅");
      } else {
        // Crear nuevo
        await axios.post("http://localhost:8080/productos", formData, {
          headers: { Authorization: `Bearer ${token}` },
        });
        alert("Producto publicado 🎉");
      }
      navigate("/mispublicaciones"); // Volver a la lista
    } catch (err) {
      console.error("❌ Error al guardar producto:", err.response || err);
      alert("Hubo un error al guardar el producto. Revisa la consola.");
    }
  };

  return (
    <div style={styles.container}>
      <h2>{id ? "Editar Producto" : "Publicar Nuevo Producto"}</h2>
      <form onSubmit={handleSubmit} style={styles.form}>
        <input
          type="text"
          name="nombre"
          placeholder="Nombre del producto"
          value={formData.nombre}
          onChange={handleChange}
          style={styles.input}
          required
        />
        <textarea
          name="descripcion"
          placeholder="Descripción"
          value={formData.descripcion}
          onChange={handleChange}
          style={styles.textarea}
          required
        />
        <input
          type="number"
          name="numeroHoras"
          placeholder="Precio en horas"
          value={formData.numeroHoras}
          onChange={handleChange}
          style={styles.input}
          required
        />
        <button type="submit" style={styles.button}>
          {id ? "Actualizar Producto" : "Publicar Producto"}
        </button>
      </form>
    </div>
  );
};

const styles = {
  container: {
    maxWidth: "500px",
    margin: "40px auto",
    padding: "20px",
    backgroundColor: "#fff",
    borderRadius: "10px",
    boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
    fontFamily: "Arial, sans-serif",
  },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "15px",
  },
  input: {
    padding: "10px",
    borderRadius: "6px",
    border: "1px solid #ccc",
    fontSize: "14px",
  },
  textarea: {
    padding: "10px",
    borderRadius: "6px",
    border: "1px solid #ccc",
    fontSize: "14px",
    minHeight: "80px",
  },
  button: {
    backgroundColor: "#28a745",
    color: "#fff",
    border: "none",
    padding: "10px",
    borderRadius: "6px",
    cursor: "pointer",
  },
};

export default ProductoForm;
