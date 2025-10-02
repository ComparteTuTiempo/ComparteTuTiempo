import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../utils/AuthContext";
import { guardarAcuerdo } from "../services/intercambioService";

const FormularioAcuerdo = () => {
  const { id } = useParams();
  const { token } = useAuth();
  const navigate = useNavigate();

  const [horasAsignadas, setHorasAsignadas] = useState("");
  const [terminos, setTerminos] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await guardarAcuerdo(id, { horasAsignadas, terminos }, token);
      alert("Acuerdo guardado correctamente ✅");
      navigate("/intercambios/usuario");
    } catch (error) {
      console.error("Error al guardar acuerdo:", error);
      alert("Hubo un error al guardar el acuerdo");
    }
  };

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h2 style={styles.title}>🤝 Establecer Acuerdo</h2>

        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.formGroup}>
            <label style={styles.label}>Número de horas:</label>
            <input
              type="number"
              value={horasAsignadas}
              onChange={(e) => setHorasAsignadas(e.target.value)}
              required
              style={styles.input}
            />
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>Términos del acuerdo:</label>
            <textarea
              value={terminos}
              onChange={(e) => setTerminos(e.target.value)}
              required
              rows="4"
              style={{ ...styles.input, resize: "vertical" }}
            />
          </div>

          <button type="submit" style={styles.submitBtn}>
            Guardar acuerdo
          </button>
        </form>
      </div>
    </div>
  );
};

const styles = {
  page: {
    display: "flex",
    justifyContent: "center",
    alignItems: "flex-start",
    backgroundColor: "#f9f9f9",
    minHeight: "100vh",
    padding: "40px 20px",
    fontFamily: "Arial, sans-serif",
  },
  card: {
    backgroundColor: "#fff",
    padding: "30px",
    borderRadius: "12px",
    boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
    width: "100%",
    maxWidth: "500px",
  },
  title: {
    fontSize: "22px",
    fontWeight: "bold",
    marginBottom: "20px",
    color: "#333",
    textAlign: "center",
  },
  form: { display: "flex", flexDirection: "column", gap: "20px" },
  formGroup: { display: "flex", flexDirection: "column", gap: "6px" },
  label: { fontSize: "14px", fontWeight: "bold", color: "#555" },
  input: {
    padding: "10px",
    border: "1px solid #ccc",
    borderRadius: "6px",
    fontSize: "14px",
    outline: "none",
    transition: "border-color 0.2s",
  },
  submitBtn: {
    padding: "12px",
    backgroundColor: "#ff6f00",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontWeight: "bold",
    fontSize: "15px",
    transition: "background 0.3s",
  },
};

export default FormularioAcuerdo;
