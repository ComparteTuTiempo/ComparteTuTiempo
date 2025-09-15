import React, { useEffect, useState } from "react";
import axios from "axios";

const IntercambiosPage = () => {
  const [intercambios, setIntercambios] = useState([]);
  const [intercambioSeleccionado, setIntercambioSeleccionado] = useState(null);

  useEffect(() => {
    const obtenerTodosLosIntercambios = async () => {
      try {
        const response = await axios.get("http://localhost:8080/intercambios");
        setIntercambios(response.data);
      } catch (error) {
        console.error("Error al obtener los intercambios:", error);
      }
    };

    obtenerTodosLosIntercambios();
  }, []);

  const abrirDetalle = (intercambio) => {
    setIntercambioSeleccionado(intercambio);
  };

  const cerrarDetalle = () => {
    setIntercambioSeleccionado(null);
  };

  return (
    <div style={{ padding: "20px" }}>
      <h1>Intercambios disponibles</h1>
      <div style={{
        display: "grid",
        gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))",
        gap: "20px",
        marginTop: "20px"
      }}>
        {intercambios.map((intercambio) => (
          <div
            key={intercambio.id}
            onClick={() => abrirDetalle(intercambio)}
            style={{
              border: "1px solid #ccc",
              borderRadius: "8px",
              padding: "15px",
              boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
              cursor: "pointer",
              transition: "transform 0.2s",
            }}
            onMouseEnter={(e) => e.currentTarget.style.transform = "scale(1.03)"}
            onMouseLeave={(e) => e.currentTarget.style.transform = "scale(1)"}
          >
            <h3>{intercambio.titulo}</h3>
            <p>{intercambio.descripcion}</p>
            <p><strong>{intercambio.horas}</strong> horas</p>
          </div>
        ))}
      </div>

      {/* Modal de detalle */}
      {intercambioSeleccionado && (
        <div style={{
          position: "fixed",
          top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: "rgba(0,0,0,0.5)",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          zIndex: 1000
        }}>
          <div style={{
            backgroundColor: "#fff",
            padding: "30px",
            borderRadius: "10px",
            width: "400px",
            maxHeight: "80%",
            overflowY: "auto",
            position: "relative"
          }}>
            <button
              onClick={cerrarDetalle}
              style={{
                position: "absolute",
                top: "10px",
                right: "10px",
                border: "none",
                background: "none",
                fontSize: "18px",
                cursor: "pointer"
              }}
            >
              ✖
            </button>
            <h2>{intercambioSeleccionado.titulo}</h2>
            <p><strong>Descripción:</strong> {intercambioSeleccionado.descripcion}</p>
            <p><strong>Horas:</strong> {intercambioSeleccionado.horas}</p>
            <p><strong>Tipo de intercambio:</strong> Oferta</p>
            <p><strong>Modalidad:</strong> {intercambioSeleccionado.modalidad}</p>
          </div>
        </div>
      )}
    </div>
  );
};

export default IntercambiosPage;
