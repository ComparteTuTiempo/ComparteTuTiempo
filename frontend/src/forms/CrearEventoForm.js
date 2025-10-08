import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";
import { crearEvento } from "../services/eventoService";
import { useNavigate } from "react-router-dom";

const CrearEventoForm = () => {
  const navigate = useNavigate();
  const { user,token} = useAuth();
  const [fechaError, setFechaError] = useState("");

  const [formData, setFormData] = useState({
    nombre: "",
    descripcion: "",
    fechaEvento: "",
    duracion: 1,
    ubicacion: "",
    capacidad: 0,
    correoOrganizador: user?.correo,
  });

  useEffect(() => {
    if (user) {
      setFormData((prev) => ({
        ...prev,
        correoOrganizador: user.correo,
      }));
    }
  }, [user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    if (name === "fechaEvento") {
      setFechaError("");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (new Date(formData.fechaEvento) <= new Date()) {
      setFechaError("La fecha del evento debe ser futura");
      return;
    }
    try {
      await crearEvento(formData, token);
      alert("Evento creado con éxito");
      setFormData({
        nombre: "",
        descripcion: "",
        fechaEvento: "",
        duracion: 1,
        ubicacion: "",
        correoOrganizador: user?.correo,
      }
      );
      navigate("/eventos");
    } catch (error) {
      console.error("Error creando el evento:", error);
      alert("Error al crear el evento");
    }
  };

  if (!user) {
    return (
      <p style={{ textAlign: "center", marginTop: "2rem" }}>
        Cargando formulario...
      </p>
    );
  }

  return (
    <div style={styles.container}>
      <form onSubmit={handleSubmit} style={styles.card}>
        <h2 style={styles.title}>Crear Evento</h2>

        <label style={styles.label}>Nombre del evento</label>
        <input
          type="text"
          name="nombre"
          value={formData.nombre}
          onChange={handleChange}
          style={styles.input}
          placeholder="Ej: Taller de cocina"
          required
        />

        <label style={styles.label}>Descripción</label>
        <textarea
          name="descripcion"
          value={formData.descripcion}
          onChange={handleChange}
          style={styles.textarea}
          placeholder="Describe tu evento..."
          required
        ></textarea>

        <label style={styles.label}>Ubicación</label>
        <input
          type="text"
          name="ubicacion"
          value={formData.ubicacion}
          onChange={handleChange}
          style={styles.input}
          placeholder="Ej: Calle Mayor 123, Madrid"
          required
        />

        <label style={styles.label}>Fecha y hora</label>
        <input
          type="datetime-local"
          name="fechaEvento"
          value={formData.fechaEvento}
          onChange={handleChange}
          style={styles.input}
          required
        />
        {fechaError && <p style={styles.error}>{fechaError}</p>}

        <label style={styles.label}>Número de horas</label>
        <input
          type="number"
          name="duracion"
          value={formData.duracion}
          onChange={handleChange}
          style={styles.input}
          min="1"
          step="0.5"
          required
        />

        <label style={styles.label}>Capacidad</label>
        <input
          type="number"
          name="capacidad"
          value={formData.capacidad}
          onChange={handleChange}
          style={styles.input}
          min="1"
          step="0.5"
          required
        />

        <button type="submit" style={styles.button}>
          Crear evento
        </button>
      </form>
    </div>
  );
};

const styles = {
  container: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    minHeight: "100vh",
    background: "#f5f7fa",
  },
  card: {
    background: "white",
    padding: "2rem",
    borderRadius: "12px",
    boxShadow: "0px 4px 12px rgba(0,0,0,0.1)",
    width: "100%",
    maxWidth: "400px",
  },
  title: {
    textAlign: "center",
    fontSize: "1.5rem",
    marginBottom: "1.5rem",
    color: "#333",
  },
  label: {
    display: "block",
    fontWeight: "bold",
    marginBottom: "0.5rem",
    color: "#444",
  },
  input: {
    width: "100%",
    padding: "0.6rem",
    marginBottom: "1rem",
    border: "1px solid #ccc",
    borderRadius: "6px",
    fontSize: "1rem",
  },
  textarea: {
    width: "100%",
    padding: "0.6rem",
    marginBottom: "1rem",
    border: "1px solid #ccc",
    borderRadius: "6px",
    fontSize: "1rem",
    resize: "vertical",
  },
  button: {
    width: "100%",
    padding: "0.8rem",
    fontSize: "1rem",
    fontWeight: "bold",
    color: "white",
    backgroundColor: "#007bff",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    transition: "0.3s",
  },
};

export default CrearEventoForm;
