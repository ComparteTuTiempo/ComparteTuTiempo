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

// En Layout.js dentro de styles
const styles = {
  header: {
    display: "flex",
    justifyContent: "space-between",
    padding: "20px 50px",
    borderBottom: "3px solid #000",   // 🔹 negro
    alignItems: "center",
    position: "sticky",
    top: 0,
    backgroundColor: "#000",          // 🔹 negro de fondo
    zIndex: 100,
  },
  logo: {
    color: "#fff",                    // 🔹 logo en blanco
    fontSize: "28px",
  },
  navLink: {
    color: "#fff",                    // 🔹 links en blanco
    marginLeft: "20px",
    textDecoration: "none",
    fontWeight: "bold",
  },
  profileImg: {
    width: "40px",
    height: "40px",
    borderRadius: "50%",
    border: "2px solid #fff",         // 🔹 borde blanco
    marginRight: "10px",
  },
  profileIcon: {
    fontSize: "32px",
    color: "#fff",                    // 🔹 icono blanco
    marginRight: "10px",
  },
  username: {
    marginRight: "15px",
    fontWeight: "bold",
    color: "#fff",                    // 🔹 texto usuario blanco
  },
  loginBtn: {
    marginLeft: "20px",
    padding: "8px 15px",
    backgroundColor: "#fff",          // 🔹 botón blanco
    color: "#000",                    // 🔹 texto negro
    border: "none",
    borderRadius: "5px",
    fontWeight: "bold",
    cursor: "pointer",
    textDecoration: "none",
  },
  logoutBtn: {
    padding: "8px 15px",
    backgroundColor: "#fff",          // 🔹 botón blanco
    color: "#000",                    // 🔹 texto negro
    border: "none",
    borderRadius: "5px",
    fontWeight: "bold",
    cursor: "pointer",
  },
};

export default Layout;
