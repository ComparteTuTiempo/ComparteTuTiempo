import React, { useEffect, useState } from "react";
import { useAuth } from "../utils/AuthContext";
import { obtenerMisIntercambios } from "../services/intercambioService";

const IntercambiosPorEstado = () => {
  const { token } = useAuth();
  const [intercambios, setIntercambios] = useState([]);
  const [seleccionado, setSeleccionado] = useState(null);
  const [tabActiva, setTabActiva] = useState("CONSENSO"); // Estado activo

  useEffect(() => {
    if (!token) return;

    const fetchIntercambios = async () => {
      try {
        const data = await obtenerMisIntercambios(token);
        setIntercambios(data);
      } catch (error) {
        console.error("Error al obtener intercambios:", error);
      }
    };

    fetchIntercambios();
  }, [token]);

  // Filtrar por estado
  const filtrados = intercambios.filter(
    (i) => (i.estado || "").toUpperCase() === tabActiva.toUpperCase()
  );

  return (
    <div style={{ padding: "20px" }}>
      <h1>Mis Intercambios</h1>

      {/* Pestañas por estado */}
      <div style={{ display: "flex", marginBottom: "20px" }}>
        {["CONSENSO", "EJECUCION"].map((estado) => (
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

      {/* Lista de intercambios filtrados */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))",
          gap: "20px",
          marginTop: "10px",
        }}
      >
        {filtrados.map((i) => (
          <div
            key={i.id}
            onClick={() => setSeleccionado(i)}
            style={{
              border: "1px solid #ccc",
              borderRadius: "8px",
              padding: "15px",
              cursor: "pointer",
              boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
            }}
          >
            <h3>{i.titulo}</h3>
            <p>{i.descripcion}</p>
            <p>
              <strong>{i.horas}</strong> horas
            </p>
          </div>
        ))}
      </div>

      {/* Modal de detalle */}
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
            <h2>{seleccionado.titulo}</h2>
            <p>
              <strong>Descripción:</strong> {seleccionado.descripcion}
            </p>
            <p>
              <strong>Horas:</strong> {seleccionado.horas}
            </p>
            <p>
              <strong>Estado:</strong> {seleccionado.estado}
            </p>
            <p>
              <strong>Tipo de intercambio:</strong> {seleccionado.tipoIntercambio}
            </p>
            <p>
              <strong>Modalidad:</strong> {seleccionado.modalidad}
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

export default IntercambiosPorEstado;
