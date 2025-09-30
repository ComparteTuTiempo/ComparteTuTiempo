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
    categorias: [], // ids de categorías seleccionadas
  });

  const [categoriasDisponibles, setCategoriasDisponibles] = useState([]);

  useEffect(() => {
    const fetchCategorias = async () => {
      try {
        const res = await axios.get("http://localhost:8080/categorias", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setCategoriasDisponibles(res.data);
      } catch (err) {
        console.error("❌ Error al cargar categorías:", err);
      }
    };

    const fetchIntercambio = async () => {
      if (id) {
        try {
          const res = await axios.get(`http://localhost:8080/intercambios/${id}`, {
            headers: { Authorization: `Bearer ${token}` },
          });

          const data = {
            ...res.data,
            categorias: res.data.categorias
              ? res.data.categorias.map((c) => c.id)
              : [],
          };
          setFormData(data);
        } catch (err) {
          console.error("❌ Error al cargar intercambio:", err);
        }
      }
    };

    fetchCategorias();
    fetchIntercambio();
  }, [id, token]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // 🔹 Toggle de categorías
  const handleToggleCategoria = (id) => {
    setFormData((prev) => {
      const alreadySelected = prev.categorias.includes(id);
      return {
        ...prev,
        categorias: alreadySelected
          ? prev.categorias.filter((c) => c !== id) // quitar
          : [...prev.categorias, id], // añadir
      };
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (id) {
        await axios.put(`http://localhost:8080/intercambios/${id}`, formData, {
          headers: { Authorization: `Bearer ${token}` },
        });
      } else {
        await axios.post(
          `http://localhost:8080/intercambios/${user.correo}`,
          formData,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
      }
      navigate("/mispublicaciones");
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
        />
        {formData.tipo === "PETICION" ? (
          <input
            type="number"
            name="numeroHoras"
            placeholder="Número de horas"
            value={formData.numeroHoras}
            onChange={handleChange}
            style={styles.input}
          />
        ) : null}

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
          <option value="VIRTUAL">Online</option>
          <option value="PRESENCIAL">Presencial</option>
        </select>

        {/* 🔹 Botones de categorías */}
        <label>Categorías:</label>
        <div style={styles.categoriasContainer}>
          {categoriasDisponibles.map((c) => {
            const isSelected = formData.categorias.includes(c.id);
            return (
              <button
                type="button"
                key={c.id}
                onClick={() => handleToggleCategoria(c.id)}
                style={{
                  ...styles.categoriaBtn,
                  ...(isSelected ? styles.categoriaBtnSelected : {}),
                }}
              >
                {c.nombre}
              </button>
            );
          })}
        </div>

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
  categoriasContainer: {
    display: "flex",
    flexWrap: "wrap",
    gap: "10px",
  },
  categoriaBtn: {
    padding: "6px 12px",
    border: "1px solid #ccc",
    borderRadius: "20px",
    backgroundColor: "#f8f9fa",
    cursor: "pointer",
  },
  categoriaBtnSelected: {
    backgroundColor: "#007bff",
    color: "#fff",
    borderColor: "#007bff",
  },
};

export default IntercambioForm;
