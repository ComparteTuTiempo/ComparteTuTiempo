import React, { useEffect, useState } from "react";
import axios from "axios";

const IntercambiosPage = () => {
  const [intercambios, setIntercambios] = useState([]);
  const [seleccionado, setSeleccionado] = useState(null);
  const [tabActiva, setTabActiva] = useState("OFERTA"); // pestaña activa

  useEffect(() => {
    const obtenerIntercambios = async () => {
      try {
        const response = await axios.get("http://localhost:8080/intercambios");
        setIntercambios(response.data);
      } catch (error) {
        console.error("Error al obtener intercambios:", error);
      }
    };

    obtenerIntercambios();
  }, []);

  const filtrados = intercambios.filter(
    (i) => (i.tipo || "").toLowerCase() === tabActiva.toLowerCase()
  );

  return (
    <div style={{ padding: "20px" }}>
      <h1>Intercambios</h1>

      {/* Pestañas */}
      <div style={{ display: "flex", marginBottom: "20px" }}>
        {["OFERTA", "PETICION"].map((tipo) => (
          <button
            key={tipo}
            onClick={() => setTabActiva(tipo)}
            style={{
              flex: 1,
              padding: "10px",
              border: "none",
              cursor: "pointer",
              backgroundColor: tabActiva === tipo ? "#007bff" : "#f1f1f1",
              color: tabActiva === tipo ? "white" : "black",
              fontWeight: tabActiva === tipo ? "bold" : "normal",
              borderRadius: "5px 5px 0 0",
            }}
          >
            {tipo}
          </button>
        ))}
      </div>

      {/* Lista */}
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
            <h3>{i.nombre}</h3>
            <p>{i.descripcion}</p>
            {i.numeroHoras && <p><strong>{i.numeroHoras}</strong> horas</p>}
            {i.categorias?.length > 0 && (
              <p>
                <strong>Categorías:</strong>{" "}
                {i.categorias.map((c) => c.nombre).join(", ")}
              </p>
            )}
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
            <h2>{seleccionado.nombre}</h2>
            <p><strong>Descripción:</strong> {seleccionado.descripcion}</p>
            {seleccionado.numeroHoras && (
              <p><strong>Horas:</strong> {seleccionado.numeroHoras}</p>
            )}
            <p><strong>Tipo:</strong> {seleccionado.tipo}</p>
            <p><strong>Modalidad:</strong> {seleccionado.modalidad}</p>
            {seleccionado.categorias?.length > 0 && (
              <p>
                <strong>Categorías:</strong>{" "}
                {seleccionado.categorias.map((c) => c.nombre).join(", ")}
              </p>
            )}
            {seleccionado.fechaPublicacion && (
              <p>
                <strong>Publicado el:</strong>{" "}
                {new Date(seleccionado.fechaPublicacion).toLocaleDateString()}
              </p>
            )}
            {seleccionado.user && (
              <p><strong>Publicado por:</strong> {seleccionado.user.nombre}</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default IntercambiosPage;
