import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { obtenerProductoPorId } from "../services/productoService";

export default function DetalleProducto() {
  const { id } = useParams();
  const [producto, setProducto] = useState(null);

  useEffect(() => {
    obtenerProductoPorId(id)
      .then(setProducto)
      .catch((err) => console.error("Error al cargar producto", err));
  }, [id]);

  if (!producto) return <p style={{ textAlign: "center" }}>Cargando...</p>;

  // 🎨 Estilos según estado
  const getEstadoStyle = (estado) => {
    switch (estado) {
      case "DISPONIBLE":
        return { backgroundColor: "#28a745", color: "white" }; // verde
      case "RESERVADO":
        return { backgroundColor: "#ffc107", color: "black" }; // amarillo
      case "ENTREGADO":
        return { backgroundColor: "#dc3545", color: "white" }; // rojo
      default:
        return { backgroundColor: "#6c757d", color: "white" }; // gris
    }
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        marginTop: "40px",
        padding: "20px",
      }}
    >
      <div
        style={{
          maxWidth: "600px",
          width: "100%",
          background: "#fff",
          borderRadius: "10px",
          padding: "30px",
          boxShadow: "0 4px 10px rgba(0,0,0,0.1)",
        }}
      >
        <h1 style={{ marginBottom: "10px" }}>{producto.nombre}</h1>

        {/* Badge de estado */}
        <span
          style={{
            display: "inline-block",
            padding: "6px 12px",
            borderRadius: "20px",
            fontSize: "14px",
            fontWeight: "bold",
            ...getEstadoStyle(producto.estado),
          }}
        >
          {producto.estado}
        </span>

        <p style={{ marginTop: "20px", fontSize: "16px" }}>
          {producto.descripcion}
        </p>

        <p>
          <strong>Horas:</strong> {producto.numeroHoras}
        </p>
        <p>
          <strong>Publicado por:</strong> {producto.usuarioNombre} (
          {producto.usuarioCorreo})
        </p>
        <p>
          <strong>Publicado el:</strong>{" "}
          {new Date(producto.fechaPublicacion).toLocaleDateString()}
        </p>
      </div>
    </div>
  );
}

