import { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";

const ReportesPage = () => {
  const { token, user } = useAuth();
  const [reportes, setReportes] = useState([]);
  const [selectedReporte, setSelectedReporte] = useState(null);

  // 🔒 Solo ADMIN debería poder acceder
  useEffect(() => {
    if (token && user?.roles?.includes("ADMIN")) {
      cargarReportes();
    }
  }, [token, user]);

  const cargarReportes = async () => {
    try {
      const res = await axios.get(`${process.env.REACT_APP_API_URL}/api/reportes/pendientes`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setReportes(res.data);
    } catch (err) {
      console.error("❌ Error al cargar reportes:", err);
    }
  };

  const confirmarReporte = async (id) => {
    if (!window.confirm("¿Seguro que quieres confirmar este reporte? El usuario será baneado.")) return;
    try {
      await axios.post(`${process.env.REACT_APP_API_URL}/api/reportes/${id}/confirmar`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("✅ Reporte confirmado, usuario baneado.");
      setSelectedReporte(null);
      cargarReportes();
    } catch (err) {
      console.error("❌ Error al confirmar reporte:", err);
      alert("Hubo un error al confirmar el reporte");
    }
  };

  const rechazarReporte = async (id) => {
    if (!window.confirm("¿Seguro que quieres rechazar este reporte?")) return;
    try {
      await axios.post(`${process.env.REACT_APP_API_URL}/api/reportes/${id}/rechazar`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("⚠️ Reporte rechazado.");
      setSelectedReporte(null);
      cargarReportes();
    } catch (err) {
      console.error("❌ Error al rechazar reporte:", err);
      alert("Hubo un error al rechazar el reporte");
    }
  };

  return (
    <div style={styles.container}>
      <h2>📋 Reportes pendientes</h2>

      <div style={styles.layout}>
        {/* Lista de reportes */}
        <div style={styles.list}>
          {reportes.length > 0 ? (
            reportes.map((r) => (
              <div
                key={r.id}
                style={styles.listItem}
                onClick={() => setSelectedReporte(r)}
              >
                <strong>{r.titulo}</strong>
                <p style={{ margin: "4px 0", fontSize: "13px", color: "#666" }}>
                  Reportado: {r.usuarioReportado?.nombre || "Desconocido"}
                </p>
              </div>
            ))
          ) : (
            <p>No hay reportes pendientes ✅</p>
          )}
        </div>

        {/* Panel de detalles */}
        {selectedReporte && (
          <div style={styles.detail}>
            <h3>{selectedReporte.titulo}</h3>
            <p><strong>Descripción:</strong> {selectedReporte.descripcion}</p>
            <p><strong>Usuario reportado:</strong> {selectedReporte.usuarioReportado?.nombre} ({selectedReporte.usuarioReportado?.correo})</p>
            <p><strong>Reportado por:</strong> {selectedReporte.usuarioReportador?.nombre} ({selectedReporte.usuarioReportador?.correo})</p>
            <p><strong>Fecha:</strong> {new Date(selectedReporte.fechaCreacion).toLocaleString()}</p>

            <div style={{ marginTop: "15px", display: "flex", gap: "10px" }}>
              <button style={styles.confirmBtn} onClick={() => confirmarReporte(selectedReporte.id)}>
                ✅ Confirmar (Banear)
              </button>
              <button style={styles.rejectBtn} onClick={() => rechazarReporte(selectedReporte.id)}>
                ❌ Rechazar
              </button>
              <button style={styles.closeBtn} onClick={() => setSelectedReporte(null)}>
                Cerrar
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

const styles = {
  container: { padding: "20px", fontFamily: "Arial, sans-serif" },
  layout: { display: "flex", gap: "20px" },
  list: {
    flex: 1,
    border: "1px solid #ddd",
    borderRadius: "8px",
    padding: "10px",
    backgroundColor: "#fff",
    maxHeight: "70vh",
    overflowY: "auto",
  },
  listItem: {
    padding: "10px",
    borderBottom: "1px solid #eee",
    cursor: "pointer",
  },
  detail: {
    flex: 2,
    border: "1px solid #ddd",
    borderRadius: "8px",
    padding: "20px",
    backgroundColor: "#fff",
    boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
  },
  confirmBtn: {
    backgroundColor: "#28a745",
    border: "none",
    color: "#fff",
    padding: "10px 16px",
    borderRadius: "6px",
    cursor: "pointer",
  },
  rejectBtn: {
    backgroundColor: "#dc3545",
    border: "none",
    color: "#fff",
    padding: "10px 16px",
    borderRadius: "6px",
    cursor: "pointer",
  },
  closeBtn: {
    backgroundColor: "#6c757d",
    border: "none",
    color: "#fff",
    padding: "10px 16px",
    borderRadius: "6px",
    cursor: "pointer",
  },
};

export default ReportesPage;
