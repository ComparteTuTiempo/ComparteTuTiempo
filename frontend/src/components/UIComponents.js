import React from "react";

// Componente Card reutilizable
export const Card = ({ children, style }) => (
  <div style={{ borderRadius: "12px", padding: "20px", backgroundColor: "#111", boxShadow: "0 0 15px #ff6f00", ...style }}>
    {children}
  </div>
);

export const CardContent = ({ children, style }) => (
  <div style={{ display: "flex", flexDirection: "column", ...style }}>{children}</div>
);

// Componente Button reutilizable
export const Button = ({ children, style, ...props }) => (
  <button
    style={{
      padding: "12px",
      borderRadius: "6px",
      border: "none",
      backgroundColor: "#ff6f00",
      color: "#fff",
      fontWeight: "bold",
      cursor: "pointer",
      transition: "0.3s",
      ...style,
    }}
    {...props}
  >
    {children}
  </button>
);

// Puedes agregar más componentes UI reutilizables aquí
