import React, { useEffect, useState } from "react";
import { Link, Outlet, useNavigate } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa";
import NotificacionesIcono from "./NotificacionesIcono";

const Layout = () => {
  const [usuario, setUsuario] = useState(null);
  const navigate = useNavigate();

  // Escucha cambios en localStorage para actualizar el estado del usuario
  useEffect(() => {
    const actualizarUsuario = () => {
      const userData = localStorage.getItem("usuario");
      if (userData) setUsuario(JSON.parse(userData));
      else setUsuario(null);
    };

    window.addEventListener("usuario-actualizado", actualizarUsuario);

    // Ejecutar al montar también
    actualizarUsuario();

    return () => window.removeEventListener("usuario-actualizado", actualizarUsuario);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("usuario");
    setUsuario(null);
    window.dispatchEvent(new Event("usuario-actualizado")); // informar al layout
    navigate("/"); // volver a landing
  };

  return (
    <div style={styles.container}>
      {/* Header */}
      <header style={styles.header}>
        <h1 style={styles.logo}>ComparteTuTiempo</h1>
        <nav style={styles.nav}>
          <Link to="/" style={styles.navLink}>
            Inicio
          </Link>
          <Link to="/registro" style={styles.navLink}>
            Registro
          </Link>

          {usuario ? (
            <div style={styles.userSection}>
              {/* Campana de notificaciones */}
               <NotificacionesIcono />
              <Link to="/perfil" style={{ textDecoration: "none" }}>
              {usuario.fotoPerfil ? (
                <img
                  src={usuario.fotoPerfil}
                  alt="perfil"
                  style={styles.profileImg}
                ></img>
              ) : (
                <FaUserCircle style={styles.profileIcon} />
              )}
              </Link>
              <span style={styles.username}>{usuario.nombre}</span>

              <button onClick={handleLogout} style={styles.logoutBtn}>
                Cerrar sesión
              </button>
            </div>
          ) : (
            <Link to="/login" style={styles.loginBtn}>
              Iniciar sesión
            </Link>
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
    borderBottom: "3px solid #ff6f00",
    alignItems: "center",
    position: "sticky",
    top: 0,
    backgroundColor: "#fff",
    zIndex: 100,
  },
  logo: {
    color: "#ff6f00",
    fontSize: "28px",
  },
  nav: {
    display: "flex",
    alignItems: "center",
  },
  navLink: {
    color: "#000",
    marginLeft: "20px",
    textDecoration: "none",
    fontWeight: "bold",
  },
  userSection: {
    display: "flex",
    alignItems: "center",
    marginLeft: "20px",
    gap:"10px"
  },
  profileImg: {
    width: "40px",
    height: "40px",
    borderRadius: "50%",
    border: "2px solid #ff6f00",
    marginRight: "10px",
  },
  profileIcon: {
    fontSize: "32px",
    color: "#ff6f00",
    marginRight: "10px",
  },
  username: {
    marginRight: "15px",
    fontWeight: "bold",
    color: "#000",
  },
  loginBtn: {
    marginLeft: "20px",
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
  main: {
    flex: 1,
    padding: "20px",
  },
  footer: {
    textAlign: "center",
    padding: "20px",
    borderTop: "3px solid #ff6f00",
    backgroundColor: "#fff",
    color: "#000",
  },
};

export default Layout;
