import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const LandingPage = () => {
  const [visibleFeatures, setVisibleFeatures] = useState([false, false, false]);

  // Animación de scroll
  useEffect(() => {
    const handleScroll = () => {
      const scrollY = window.scrollY + window.innerHeight;
      const featureElements = document.querySelectorAll(".featureBox");
      featureElements.forEach((el, idx) => {
        if (el.offsetTop < scrollY - 100) {
          setVisibleFeatures((prev) => {
            const copy = [...prev];
            copy[idx] = true;
            return copy;
          });
        }
      });
    };
    window.addEventListener("scroll", handleScroll);
    handleScroll();
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  return (
    <div style={styles.container}>
      {/* Header */}
      <header style={styles.header}>
        <h1 style={styles.logo}>ComparteTuTiempo</h1>
        <nav>
          <a href="#features" style={styles.navLink}>Características</a>
          <Link to="/registro" style={styles.navLink}>Registro</Link>
        </nav>
      </header>

      {/* Hero */}
      <section style={styles.hero}>
        <h2 style={styles.heroTitle}>Conecta y comparte tu tiempo con tu comunidad</h2>
        <p style={styles.heroText}>
          Aprender, enseñar y disfrutar de actividades con tu comunidad nunca fue tan fácil.
        </p>
        <Link to="/registro" style={styles.heroButton}>Registrarse</Link>
      </section>

      {/* Características */}
      <section id="features" style={styles.features}>
        {["Intercambio de habilidades", "Sistema de tiempo", "Eventos grupales"].map((title, idx) => (
          <div
            key={idx}
            className="featureBox"
            style={{
              ...styles.featureBox,
              opacity: visibleFeatures[idx] ? 1 : 0,
              transform: visibleFeatures[idx] ? "translateY(0)" : "translateY(50px)",
              transition: `all 0.6s ease ${idx * 0.2}s`,
            }}
          >
            <h3 style={styles.featureTitle}>{title}</h3>
            <p style={styles.featureText}>
              {title === "Intercambio de habilidades" &&
                "Aprende o enseña nuevas habilidades con otros usuarios."}
              {title === "Sistema de tiempo" &&
                "Usa horas como moneda para valorar los intercambios."}
              {title === "Eventos grupales" &&
                "Participa en actividades y eventos con tu comunidad."}
            </p>
          </div>
        ))}
      </section>

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
    color: "#000",
    backgroundColor: "#fff",
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
    transition: "0.3s",
  },
  hero: {
    textAlign: "center",
    padding: "120px 20px",
    background: "linear-gradient(135deg, #ff6f00 0%, #fff 100%)",
    backgroundSize: "cover",
    backgroundAttachment: "fixed",
    color: "#000",
  },
  heroTitle: {
    fontSize: "42px",
    marginBottom: "20px",
  },
  heroText: {
    fontSize: "20px",
    marginBottom: "30px",
  },
  heroButton: {
    padding: "15px 50px",
    background: "linear-gradient(90deg, #ff6f00 0%, #fff 100%)",
    color: "#000",
    fontSize: "18px",
    fontWeight: "bold",
    borderRadius: "10px",
    textDecoration: "none",
    transition: "0.3s",
  },
  features: {
    display: "flex",
    justifyContent: "space-around",
    padding: "100px 20px",
    flexWrap: "wrap",
    gap: "30px",
  },
  featureBox: {
    backgroundColor: "#fff",
    padding: "30px",
    borderRadius: "12px",
    width: "300px",
    boxShadow: "0 0 25px #ff6f00",
    color: "#000",
  },
  featureTitle: {
    fontSize: "22px",
    color: "#ff6f00",
    marginBottom: "15px",
  },
  featureText: {
    fontSize: "16px",
  },
  footer: {
    textAlign: "center",
    padding: "30px",
    borderTop: "3px solid #ff6f00",
    marginTop: "50px",
    backgroundColor: "#fff",
    color: "#000",
  },
};

export default LandingPage;
