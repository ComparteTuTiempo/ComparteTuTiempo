import React, { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { 
  FaUser, FaExchangeAlt, FaBook, FaCalendarAlt, 
  FaHistory, FaComments, FaBinoculars, FaChevronDown 
} from "react-icons/fa";

const Sidebar = () => {
  const location = useLocation();
  const [openIntercambios, setOpenIntercambios] = useState(false);

  const links = [
    { to: "/perfil", label: "Mi Perfil", icon: <FaUser /> },
    { to: "/mercado", label: "Mercado", icon: <FaExchangeAlt /> },
    { to: "/mispublicaciones", label: "Mis Publicaciones", icon: <FaBook /> },
    { to: "/eventos", label: "Eventos", icon: <FaCalendarAlt /> },
    { to: "/historial", label: "Historial", icon: <FaHistory /> },
    { to: "/conversaciones", label: "Chats", icon: <FaComments /> },
    { to: "/buscarusuarios", label: "Usuarios", icon: <FaBinoculars /> },
  ];

  return (
    <aside style={styles.sidebar}>
      <ul style={styles.menu}>
        {links.map((link) => (
          <li key={link.to}>
            <Link
              to={link.to}
              style={{
                ...styles.menuItem,
                ...(location.pathname.startsWith(link.to) ? styles.active : {}),
              }}
            >
              <span style={styles.icon}>{link.icon}</span>
              {link.label}
            </Link>
          </li>
        ))}

        {/* Intercambios con submenú */}
        <li>
          <div
            style={{
              ...styles.menuItem,
              cursor: "pointer",
              ...(location.pathname.startsWith("/intercambios") ||
              location.pathname.startsWith("/productousuario")
                ? styles.active
                : {}),
            }}
            onClick={() => setOpenIntercambios(!openIntercambios)}
          >
            <span style={styles.icon}>
              <FaHistory />
            </span>
            Intercambios
            <span style={{ marginLeft: "auto" }}>
              <FaChevronDown
                style={{
                  transform: openIntercambios ? "rotate(180deg)" : "rotate(0)",
                  transition: "transform 0.2s",
                }}
              />
            </span>
          </div>
          {openIntercambios && (
            <ul style={styles.subMenu}>
              <li>
                <Link
                  to="/intercambios/usuario"
                  style={{
                    ...styles.subMenuItem,
                    ...(location.pathname.startsWith("/intercambios/usuario")
                      ? styles.active
                      : {}),
                  }}
                >
                  Servicios
                </Link>
              </li>
              <li>
                <Link
                  to="/productousuario/transacciones"
                  style={{
                    ...styles.subMenuItem,
                    ...(location.pathname.startsWith(
                      "/productousuario/transacciones"
                    )
                      ? styles.active
                      : {}),
                  }}
                >
                  Productos
                </Link>
              </li>
            </ul>
          )}
        </li>
      </ul>
    </aside>
  );
};

const styles = {
  sidebar: {
    width: "220px",
    backgroundColor: "#fff",
    borderRight: "1px solid #ddd",
    padding: "20px",
    minHeight: "100vh",
  },
  menu: { listStyle: "none", padding: 0, margin: 0 },
  menuItem: {
    display: "flex",
    alignItems: "center",
    padding: "12px 10px",
    borderRadius: "8px",
    marginBottom: "8px",
    color: "#333",
    fontWeight: "500",
    textDecoration: "none",
    transition: "background 0.2s",
  },
  active: {
    backgroundColor: "#f5f5f5",
    fontWeight: "bold",
    color: "#ff6f00",
  },
  icon: { marginRight: "10px" },
  subMenu: {
    listStyle: "none",
    paddingLeft: "20px",
    marginTop: "5px",
    marginBottom: "10px",
  },
  subMenuItem: {
    display: "block",
    padding: "8px 10px",
    borderRadius: "6px",
    marginBottom: "5px",
    color: "#555",
    textDecoration: "none",
    fontSize: "14px",
  },
};

export default Sidebar;
