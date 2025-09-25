import { useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";

const VerificacionForm = () => {
  const { user, token } = useAuth();
  const [file, setFile] = useState(null);
  const [mensaje, setMensaje] = useState("");

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!file) {
      setMensaje("Por favor selecciona un archivo");
      return;
    }

    try {
      const formData = new FormData();
      formData.append("documentoURL", file); // el nombre debe coincidir con @RequestParam("documentoURL")

      await axios.post(
        `http://localhost:8080/api/verificaciones/${user.correo}`, // correo en la URL
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setMensaje("✅ Solicitud enviada con éxito");
    } catch (err) {
      console.error("❌ Error al enviar verificación:", err);
      setMensaje("❌ Error al enviar la solicitud");
    }
  };

  return (
    <div style={styles.container}>
      <h2>Solicitud de Verificación</h2>
      <form onSubmit={handleSubmit} style={styles.form}>
        <input
          type="file"
          accept="image/*,.pdf"
          onChange={handleFileChange}
          style={styles.input}
        />
        <button type="submit" style={styles.button}>
          Enviar
        </button>
      </form>
      {mensaje && <p>{mensaje}</p>}
    </div>
  );
};

const styles = {
  container: {
    maxWidth: "400px",
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
  button: {
    backgroundColor: "#007bff",
    color: "#fff",
    border: "none",
    padding: "10px",
    borderRadius: "6px",
    cursor: "pointer",
  },
};

export default VerificacionForm;
