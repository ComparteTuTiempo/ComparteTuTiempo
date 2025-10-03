import React, { useState } from "react";
import { Link, Outlet, useNavigate } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa";
import { useAuth } from "../utils/AuthContext";
import NotificacionesIcono from "./NotificacionesIcono";

const Layout = () => {
  const { user } = useAuth();
  const [adminMenuOpen, setAdminMenuOpen] = useState(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("usuario");
    localStorage.removeItem("token");
    window.dispatchEvent(new Event("usuario-actualizado"));
    navigate("/");
  };

  const esAdmin = user?.roles?.includes("ADMIN");

  return (
    <div style={styles.container}>
      {/* Header */}
      <header style={styles.header}>
        <h1 style={styles.logo}>ComparteTuTiempo</h1>
        <nav style={styles.nav}>
          <Link to="/" style={styles.navLink}>Inicio</Link>

          {user ? (
            <>
              <Link to="/mispublicaciones" style={styles.navLink}>
                Mis Publicaciones
              </Link>
              <Link to="/eventos/crear" style={styles.navLink}>
                Eventos
              </Link>
              <Link to="/mercado" style={styles.navLink}>
                Intercambios
              </Link>

              {/* Menú administrador */}
              {esAdmin && (
                <div style={styles.adminMenu}>
                  <span
                    style={styles.adminLink}
                    onClick={() => setAdminMenuOpen(!adminMenuOpen)}
                  >
                    Administrador ▾
                  </span>
                  {adminMenuOpen && (
                    <div style={styles.adminDropdown}>
                      <Link to="/admin/verificaciones" style={styles.dropdownItem}>
                        Verificaciones
                      </Link>
                      <Link to="/admin/reportes" style={styles.dropdownItem}>
                        Reportes
                      </Link>
                      <Link to="/admin/categorias" style={styles.dropdownItem}>
                        Moderar Categorías
                      </Link>
                    </div>
                  )}
                </div>
              )}

              <div style={styles.userSection}>
                {/* 🔔 Campana de notificaciones */}
                <NotificacionesIcono />

                <span style={styles.username}>
                  {user.nombre || user.correo}
                </span>

                {/* 🔹 Badge de horas */}
                {user.numeroHoras !== undefined && (
                  <span style={styles.hoursBadge}>
                    {user.numeroHoras} horas
                  </span>
                )}

                <Link to="/perfil" style={{ textDecoration: "none" }}>
                  {user.fotoPerfil ? (
                    <img
                      src={user.fotoPerfil}
                      alt="perfil"
                      style={styles.profileImg}
                    />
                  ) : (
                    <FaUserCircle style={styles.profileIcon} />
                  )}
                </Link>

                <button onClick={handleLogout} style={styles.logoutBtn}>
                  Cerrar sesión
                </button>
              </div>
            </>
          ) : (
            <>
              <Link to="/registro" style={styles.navLink}>Registro</Link>
              <Link to="/login" style={styles.loginBtn}>Iniciar sesión</Link>
            </>
          )}
        </nav>
      </header>

      {/* Contenido principal */}
      <main style={styles.main}>
        <Outlet />
      </main>

      {/* Footer */}
      <footer style={styles.footer}>
        <p>© 2025 ComparteTuTiempo. Todos los derechos reservados.</p>
      </footer>
    </div>
  );
};

const styles = {
  container: {
    fontFamily: "'Arial', sans-serif",
    backgroundColor: "#fff",
    color: "#000",
    minHeight: "100vh",
    display: "flex",
    flexDirection: "column",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    padding: "20px 50px",
    borderBottom: "3px solid #000",
    alignItems: "center",
    position: "sticky",
    top: 0,
    backgroundColor: "#000",
    zIndex: 100,
  },
  logo: {
    color: "#fff",
    fontSize: "28px",
  },
  nav: {
    display: "flex",
    alignItems: "center",
    gap: "20px",
    position: "relative",
  },
  navLink: {
    color: "#fff",
    textDecoration: "none",
    fontWeight: "bold",
  },
  userSection: {
    display: "flex",
    alignItems: "center",
    marginLeft: "20px",
    gap: "10px",
  },
  profileImg: {
    width: "40px",
    height: "40px",
    borderRadius: "50%",
    border: "2px solid #fff",
    marginRight: "10px",
  },
  profileIcon: {
    fontSize: "32px",
    color: "#fff",
    marginRight: "10px",
  },
  username: {
    marginRight: "15px",
    fontWeight: "bold",
    color: "#fff",
  },
  hoursBadge: {
    backgroundColor: "#ff6f00",
    color: "#fff",
    padding: "4px 10px",
    borderRadius: "12px",
    fontSize: "13px",
    fontWeight: "bold",
  },
  loginBtn: {
    padding: "8px 15px",
    backgroundColor: "#ff6f00",
    color: "#fff",
    border: "none",
    borderRadius: "5px",
    fontWeight: "bold",
    cursor: "pointer",
    textDecoration: "none",
  },
  logoutBtn: {
    padding: "8px 15px",
    backgroundColor: "#ff6f00",
    color: "#fff",
    border: "none",
    borderRadius: "5px",
    fontWeight: "bold",
    cursor: "pointer",
  },
  main: { flex: 1, padding: "20px" },
  footer: {
    textAlign: "center",
    padding: "20px",
    borderTop: "3px solid #000",
    backgroundColor: "#000",
    color: "#fff",
  },
  adminMenu: {
    position: "relative",
    color: "#fff",
    cursor: "pointer",
    fontWeight: "bold",
  },
  adminLink: {
    color: "#fff",
    textDecoration: "none",
  },
  adminDropdown: {
    position: "absolute",
    top: "100%",
    left: 0,
    backgroundColor: "#222",
    borderRadius: "5px",
    padding: "10px",
    display: "flex",
    flexDirection: "column",
    gap: "8px",
    minWidth: "200px",
    zIndex: 200,
  },
  dropdownItem: {
    color: "#fff",
    textDecoration: "none",
    fontWeight: "normal",
    padding: "5px 10px",
    borderRadius: "4px",
  },
};

export default Layout;
