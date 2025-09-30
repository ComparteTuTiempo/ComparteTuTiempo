import React, { useEffect, useState } from "react";
import { Link, Outlet, useNavigate } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa";

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
    window.dispatchEvent(new Event("usuario-actualizado"));
    navigate("/"); // volver a landing
  };

  return (
    <div style={styles.container}>
      {/* Header */}
      <header style={styles.header}>
        <h1 style={styles.logo}>ComparteTuTiempo</h1>
        <nav style={styles.nav}>
          <Link to="/" style={styles.navLink}>Inicio</Link>

          {usuario ? (
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

              <div style={styles.userSection}>
                <Link to="/perfil" style={{ textDecoration: "none" }}>
                  {usuario.fotoPerfil ? (
                    <img
                      src={usuario.fotoPerfil}
                      alt="perfil"
                      style={styles.profileImg}
                    />
                  ) : (
                    <FaUserCircle style={styles.profileIcon} />
                  )}
                </Link>
                <span style={styles.username}>{usuario.nombre}</span>
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
};

export default Layout;
