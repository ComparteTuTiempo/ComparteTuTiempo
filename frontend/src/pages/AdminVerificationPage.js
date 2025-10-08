import { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";

const AdminVerificationPage = () => {
  const { token } = useAuth();
  const [verificaciones, setVerificaciones] = useState([]);
  const [mensaje, setMensaje] = useState("");

  useEffect(() => {
    const fetchPendientes = async () => {
      try {
        const res = await axios.get(`http://localhost:8080/api/verificaciones/pendientes`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setVerificaciones(res.data);
      } catch (err) {
        console.error("❌ Error cargando verificaciones:", err);
        setMensaje("Error cargando verificaciones pendientes");
      }
    };
    fetchPendientes();
  }, [token]);

  const handleAccion = async (id, accion) => {
    try {
      await axios.put(
        `http://localhost:8080/api/verificaciones/${id}/${accion}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setMensaje(`✅ Verificación ${accion} correctamente`);
      setVerificaciones((prev) => prev.filter((v) => v.id !== id)); // quitar de la lista
    } catch (err) {
      console.error(`❌ Error al ${accion} verificación:`, err);
      setMensaje(`❌ Error al ${accion} verificación`);
    }
  };

  return (
    <div style={styles.container}>
      <h2>Solicitudes de Verificación Pendientes</h2>
      {mensaje && <p>{mensaje}</p>}
      {verificaciones.length === 0 ? (
        <p>No hay verificaciones pendientes</p>
      ) : (
        <div style={styles.cards}>
          {verificaciones.map((v) => (
            <div key={v.id} style={styles.card}>
              <p><b>Usuario:</b> {v.usuario?.correo}</p>
              <p><b>Estado:</b> {v.estado}</p>
              <div style={{ margin: "10px 0" }}>
                <p><b>Documento:</b></p>
                {v.documentoURL ? (
                  <img
                    src={`http://localhost:8080/uploads/${v.documentoURL}`}
                    alt="Documento"
                    style={styles.image}
                  />
                ) : (
                  <p>No se subió documento</p>
                )}
              </div>
              <div style={styles.buttons}>
                <button
                  onClick={() => handleAccion(v.id, "aprobar")}
                  style={{ ...styles.button, backgroundColor: "green" }}
                >
                  Aprobar
                </button>
                <button
                  onClick={() => handleAccion(v.id, "rechazar")}
                  style={{ ...styles.button, backgroundColor: "red" }}
                >
                  Rechazar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

const styles = {
  container: { maxWidth: "900px", margin: "40px auto", padding: "20px" },
  cards: { display: "grid", gap: "20px", gridTemplateColumns: "repeat(auto-fit, minmax(300px, 1fr))" },
  card: {
    padding: "20px",
    border: "1px solid #ddd",
    borderRadius: "10px",
    background: "#fff",
    boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
  },
  image: {
    maxWidth: "100%",
    border: "1px solid #ccc",
    borderRadius: "6px",
  },
  buttons: { display: "flex", gap: "10px", marginTop: "10px" },
  button: {
    color: "#fff",
    border: "none",
    padding: "10px 15px",
    borderRadius: "6px",
    cursor: "pointer",
    flex: 1,
  },
};

export default AdminVerificationPage;
