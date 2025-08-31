import React from "react";
import { Link, Outlet } from "react-router-dom";

const Layout = () => {
  return (
    <div style={styles.container}>
      {/* Header */}
      <header style={styles.header}>
        <h1 style={styles.logo}>ComparteTuTiempo</h1>
        <nav>
          <Link to="/" style={styles.navLink}>Inicio</Link>
          <Link to="/registro" style={styles.navLink}>Registro</Link>
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
  navLink: {
    color: "#000",
    marginLeft: "20px",
    textDecoration: "none",
    fontWeight: "bold",
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
