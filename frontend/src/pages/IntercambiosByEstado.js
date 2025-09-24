import React, { useEffect, useState } from "react";
import { useAuth } from "../utils/AuthContext";
import {
  obtenerMisIntercambiosUsuario,
  finalizarAcuerdo,
} from "../services/intercambioService";
import { useNavigate } from "react-router-dom";
import SolicitudesIntercambio from "./SolicitudesIntercambioList"; 

const IntercambiosPorEstado = () => {
  const { token } = useAuth();
  const [intercambios, setIntercambios] = useState([]);
  const [seleccionado, setSeleccionado] = useState(null);
  const [tabActiva, setTabActiva] = useState("CONSENSO");
  const navigate = useNavigate();

  useEffect(() => {
    if (!token) return;

    const fetchIntercambios = async () => {
      try {
        if (tabActiva === "EMPAREJAMIENTO") return; 
        const data = await obtenerMisIntercambiosUsuario(tabActiva, token);
        setIntercambios(data);
      } catch (error) {
        console.error("Error al obtener intercambios:", error);
      }
    };

    fetchIntercambios();
  }, [token, tabActiva]);

  return (
    <div style={{ padding: "20px" }}>
      <h1>Mis Intercambios</h1>

      {/* Pestañas por estado */}
      <div style={{ display: "flex", marginBottom: "20px" }}>
        {["EMPAREJAMIENTO", "CONSENSO", "EJECUCION"].map((estado) => (
          <button
            key={estado}
            onClick={() => setTabActiva(estado)}
            style={{
              flex: 1,
              padding: "10px",
              border: "none",
              cursor: "pointer",
              backgroundColor: tabActiva === estado ? "#007bff" : "#f1f1f1",
              color: tabActiva === estado ? "white" : "black",
              fontWeight: tabActiva === estado ? "bold" : "normal",
              borderRadius: "5px 5px 0 0",
            }}
          >
            {estado}
          </button>
        ))}
      </div>

      {/* Vista para cada estado */}
      {tabActiva === "EMPAREJAMIENTO" ? (
        <SolicitudesIntercambio /> 
      ) : (
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))",
            gap: "20px",
            marginTop: "10px",
          }}
        >
          {intercambios.map((i) => (
            <div
              key={i.id}
              style={{
                border: "1px solid #ccc",
                borderRadius: "8px",
                padding: "15px",
                cursor: "pointer",
                boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
              }}
            >
              <p>
                <strong>
                  {i.intercambioNombre} - {i.solicitanteNombre}
                </strong>
              </p>

              {/* CONSENSO: botones chat y establecer acuerdo */}
              {i.estado === "CONSENSO" && (
                <div
                  style={{ marginTop: "10px", display: "flex", gap: "10px" }}
                >
                  {i.conversacionId && (
                    <button
                      onClick={() =>
                        navigate(`/conversaciones/${i.conversacionId}`)
                      }
                      style={{
                        flex: 1,
                        padding: "8px 12px",
                        backgroundColor: "#28a745",
                        color: "white",
                        border: "none",
                        borderRadius: "5px",
                        cursor: "pointer",
                      }}
                    >
                      💬 Discutir acuerdo
                    </button>
                  )}

                  <button
                    onClick={() => navigate(`/acuerdos/${i.id}`)}
                    style={{
                      flex: 1,
                      padding: "8px 12px",
                      backgroundColor: "#007bff",
                      color: "white",
                      border: "none",
                      borderRadius: "5px",
                      cursor: "pointer",
                    }}
                  >
                    📑 Establecer acuerdo
                  </button>
                </div>
              )}

              {/* EJECUCIÓN: botón finalizar */}
              {tabActiva === "EJECUCION" && (
                <button
                  onClick={async () => {
                    try {
                      await finalizarAcuerdo(i.id, token);
                      alert("✅ Acuerdo finalizado");
                      const data = await obtenerMisIntercambiosUsuario(
                        tabActiva,
                        token
                      );
                      setIntercambios(data);
                    } catch (err) {
                      alert("❌ Error al finalizar: " + err.message);
                    }
                  }}
                  style={{
                    marginTop: "10px",
                    padding: "8px 12px",
                    backgroundColor: "#dc3545",
                    color: "white",
                    border: "none",
                    borderRadius: "5px",
                    cursor: "pointer",
                  }}
                >
                  🔒 Finalizar
                </button>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Modal opcional */}
      {seleccionado && (
        <div
          style={{
            position: "fixed",
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: "rgba(0,0,0,0.5)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            zIndex: 1000,
          }}
        >
          <div
            style={{
              background: "#fff",
              padding: "30px",
              borderRadius: "10px",
              width: "400px",
              position: "relative",
            }}
          >
            <button
              onClick={() => setSeleccionado(null)}
              style={{
                position: "absolute",
                top: "10px",
                right: "10px",
                border: "none",
                background: "none",
                fontSize: "18px",
                cursor: "pointer",
              }}
            >
              ✖
            </button>
            <h2>{seleccionado.intercambioNombre}</h2>
            <p>
              <strong>Estado:</strong> {seleccionado.estado}
            </p>
            <p>
              <strong>Solicitante:</strong> {seleccionado.solicitanteNombre}
            </p>
            {seleccionado.estado === "CONSENSO" &&
              seleccionado.conversacionId && (
                <button
                  onClick={() =>
                    navigate(`/conversaciones/${seleccionado.conversacionId}`)
                  }
                  style={{
                    marginTop: "15px",
                    padding: "10px 14px",
                    backgroundColor: "#28a745",
                    color: "white",
                    border: "none",
                    borderRadius: "5px",
                    cursor: "pointer",
                  }}
                >
                  💬 Ir al chat
                </button>
              )}
          </div>
        </div>
      )}
    </div>
  );
};

export default IntercambiosPorEstado;


