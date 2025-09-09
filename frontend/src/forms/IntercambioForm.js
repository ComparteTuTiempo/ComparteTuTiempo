import { useParams, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";

const IntercambioForm = () => {
  const { id } = useParams();
  const { user, token } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    nombre: "",
    descripcion: "",
    numeroHoras: "",
    tipo: "OFERTA",
    modalidad: "PRESENCIAL",
  });

  useEffect(() => {
    if (id) {
      // estamos en modo edición
      axios.get(`http://localhost:8080/intercambios/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setFormData(res.data))
      .catch((err) => console.error("❌ Error al cargar intercambio:", err));
    }
  }, [id, token]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (id) {
        // actualizar
        await axios.put(`http://localhost:8080/intercambios/${id}`, formData, {
          headers: { Authorization: `Bearer ${token}` },
        });
      } else {
        // crear nuevo
        await axios.post(`http://localhost:8080/intercambios/${user.correo}`, formData, {
          headers: { Authorization: `Bearer ${token}` },
        });
      }
      navigate("/profile"); 
    } catch (err) {
      console.error("❌ Error al guardar intercambio:", err);
    }
  };

  return (
    <div style={styles.container}>
      <h2>{id ? "Editar Intercambio" : "Crear Nuevo Intercambio"}</h2>
      <form onSubmit={handleSubmit} style={styles.form}>
        <input
          type="text"
          name="nombre"
          placeholder="Título de la oferta"
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
        />{formData.tipo === "PETICION"?
          <input
          type="number"
          name="numeroHoras"
          placeholder="Número de horas"
          value={formData.numeroHoras}
          onChange={handleChange}
          style={styles.input}
          />
          :""}
        
        <select
          name="tipo"
          value={formData.tipo}
          onChange={handleChange}
          style={styles.input}
        >
          <option value="OFERTA">Oferta</option>
          <option value="PETICION">Petición</option>
        </select>
        <select
          name="modalidad"
          value={formData.modalidad}
          onChange={handleChange}
          style={styles.input}
        >
          <option value="ONLINE">Online</option>
          <option value="PRESENCIAL">Presencial</option>
        </select>
        <button type="submit" style={styles.button}>
          {id ? "Editar Intercambio" : "Crear Intercambio"}
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

export default IntercambioForm;
