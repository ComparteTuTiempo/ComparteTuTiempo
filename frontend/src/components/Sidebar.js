import React from "react";
import { Link, useLocation } from "react-router-dom";
import { FaUser, FaExchangeAlt, FaBook, FaCalendarAlt, FaHistory, FaComments, FaBinoculars } from "react-icons/fa";

const Sidebar = () => {
  const location = useLocation();

  const links = [
    { to: "/perfil", label: "Mi Perfil", icon: <FaUser /> },
    { to: "/mercado", label: "Intercambios", icon: <FaExchangeAlt /> },
    { to: "/mispublicaciones", label: "Mis Publicaciones", icon: <FaBook /> },
    { to: "/eventos/crear", label: "Eventos", icon: <FaCalendarAlt /> },
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
};

export default Sidebar;
