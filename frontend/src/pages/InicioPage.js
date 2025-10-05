import React from "react";
import { useNavigate } from "react-router-dom";

const InicioPage = () => {
  const navigate = useNavigate();

  return (
    <div style={styles.container}>
      {/* Hero Section */}
      <header style={styles.hero}>
        <h1 style={styles.title}>Bienvenido a ComparteTuTiempo</h1>
        <p style={styles.subtitle}>
          Descubre oportunidades de intercambios y participa en eventos únicos.
        </p>
        <div style={styles.heroButtons}>
          <button
            style={styles.primaryBtn}
            onClick={() => navigate("/mercado")}
          >
            🌟 Explorar Mercado
          </button>
          <button
            style={styles.secondaryBtn}
            onClick={() => navigate("/eventos")}
          >
            🎉 Ver Eventos
          </button>
        </div>
      </header>

      {/* Sección de Características */}
      <section style={styles.features}>
        <div style={styles.featureCard}>
          <h3>Intercambios</h3>
          <p>
            Publica, busca y gestiona intercambios de productos y servicios
            con otros usuarios.
          </p>
        </div>
        <div style={styles.featureCard}>
          <h3>Eventos</h3>
          <p>
            Participa en eventos, talleres y actividades de tu interés,
            interactuando con la comunidad.
          </p>
        </div>
        <div style={styles.featureCard}>
          <h3>Comunidad</h3>
          <p>
            Conecta con otros usuarios, construye tu reputación y comparte
            habilidades.
          </p>
        </div>
      </section>
      
      <footer style={styles.footer}>
        <p>© 2025 ComparteTuTiempo. Todos los derechos reservados.</p>
      </footer>
    </div>
  );
};

const styles = {
  container: {
    fontFamily: "'Arial', sans-serif",
    color: "#333",
    margin: 0,
    padding: 0,
  },
  hero: {
    background: "linear-gradient(135deg, #d0eaff 0%, #a0d4ff 100%)",
    color: "#000",
    textAlign: "center",
    padding: "100px 20px",
  },
  title: {
    fontSize: "48px",
    marginBottom: "20px",
  },
  subtitle: {
    fontSize: "20px",
    marginBottom: "40px",
  },
  heroButtons: {
    display: "flex",
    justifyContent: "center",
    gap: "20px",
    flexWrap: "wrap",
  },
  primaryBtn: {
    padding: "12px 30px",
    backgroundColor: "#ff6f00",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    fontSize: "16px",
    cursor: "pointer",
    fontWeight: "bold",
  },
  secondaryBtn: {
    padding: "12px 30px",
    backgroundColor: "#007bff",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    fontSize: "16px",
    cursor: "pointer",
    fontWeight: "bold",
  },
  features: {
    display: "flex",
    justifyContent: "center",
    gap: "20px",
    padding: "60px 20px",
    flexWrap: "wrap",
    backgroundColor: "#f9f9f9",
  },
  featureCard: {
    flex: "1 1 250px",
    background: "#fff",
    padding: "30px",
    borderRadius: "12px",
    boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
    textAlign: "center",
    transition: "transform 0.2s, box-shadow 0.2s",
  },
  ctaSection: {
    textAlign: "center",
    padding: "80px 20px",
    backgroundColor: "#a0d4ff",
    color: "#000",
  },
  primaryBtnLarge: {
    marginTop: "20px",
    padding: "16px 40px",
    backgroundColor: "#ff6f00",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    fontSize: "18px",
    fontWeight: "bold",
    cursor: "pointer",
  },
  footer: {
    textAlign: "center",
    padding: "30px 20px",
    backgroundColor: "#f1f1f1",
    color: "#555",
  },
};

export default InicioPage;
