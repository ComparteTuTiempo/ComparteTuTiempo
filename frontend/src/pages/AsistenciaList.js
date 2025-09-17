import React, { useState, useEffect } from "react";
import { useAuth } from "../utils/AuthContext";
import { useParams } from "react-router-dom";
import {
  cargarParticipantes,
  marcarAsistencia,
  finalizarEvento,
} from "../services/eventoService";

const ListaAsistencia = () => {
  const { user } = useAuth();
  const { id } = useParams();
  const [participantes, setParticipantes] = useState([]);
  const [filtro, setFiltro] = useState("");

  useEffect(() => {
    if (user) cargar();
  }, [user]);

  const cargar = async () => {
    try {
      const data = await cargarParticipantes(id, user.correo);
      setParticipantes(data);
    } catch (error) {
      alert(error.response?.data || "Error al cargar participantes");
    }
  };

  const handleMarcarAsistencia = async (correoParticipante, asistio) => {
    try {
      await marcarAsistencia(id, user.correo, correoParticipante, asistio);
      cargar();
    } catch (error) {
      alert(error.response?.data || "Error al marcar asistencia");
    }
  };

  const handleFinalizarEvento = async () => {
    if (!window.confirm("¿Seguro que quieres finalizar el evento?")) return;

    try {
      await finalizarEvento(id, user.correo);
      alert("Evento finalizado correctamente");
    } catch (error) {
      alert(error.response?.data || "Error al finalizar evento");
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>Gestión de Asistencia</h2>

      <input
        type="text"
        placeholder="Buscar por nombre..."
        value={filtro}
        onChange={(e) => setFiltro(e.target.value)}
        style={styles.input}
      />

      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>Correo</th>
            <th style={styles.th}>Nombre</th>
            <th style={styles.th}>Asistencia</th>
          </tr>
        </thead>
        <tbody>
          {participantes
            .filter((p) =>
              p.nombre.toLowerCase().includes(filtro.toLowerCase())
            )
            .map((p) => (
              <tr key={p.correo}>
                <td style={styles.td}>{p.correo}</td>
                <td style={styles.td}>{p.nombre}</td>
                <td style={styles.td}>
                  <label style={styles.radioLabel}>
                    <input
                      type="radio"
                      name={`asistencia-${p.correo}`}
                      checked={p.asistio === true}
                      onChange={() =>
                        handleMarcarAsistencia(p.correo, true)
                      }
                    />
                    Sí
                  </label>
                  <label style={styles.radioLabel}>
                    <input
                      type="radio"
                      name={`asistencia-${p.correo}`}
                      checked={p.asistio === false}
                      onChange={() =>
                        handleMarcarAsistencia(p.correo, false)
                      }
                    />
                    No
                  </label>
                </td>
              </tr>
            ))}
        </tbody>
      </table>

      <button onClick={handleFinalizarEvento} style={styles.button}>
        Finalizar Evento
      </button>
    </div>
  );
};

const styles = {
  container: {
    maxWidth: "800px",
    margin: "2rem auto",
    padding: "1.5rem",
    background: "#fff",
    borderRadius: "12px",
    boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
  },
  title: {
    textAlign: "center",
    marginBottom: "1rem",
    color: "#333",
  },
  input: {
    width: "100%",
    padding: "0.5rem",
    marginBottom: "1rem",
    border: "1px solid #ccc",
    borderRadius: "6px",
  },
  table: {
    width: "100%",
    borderCollapse: "collapse",
    marginBottom: "1rem",
  },
  th: {
    textAlign: "left",
    padding: "0.75rem",
    borderBottom: "2px solid #ddd",
    background: "#f8f9fa",
  },
  td: {
    padding: "0.75rem",
    borderBottom: "1px solid #eee",
  },
  radioLabel: {
    marginRight: "1rem",
    display: "inline-flex",
    alignItems: "center",
    gap: "0.3rem",
    cursor: "pointer",
  },
  button: {
    width: "100%",
    padding: "0.75rem",
    backgroundColor: "#007bff",
    color: "white",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontWeight: "bold",
  },
};

export default ListaAsistencia;


