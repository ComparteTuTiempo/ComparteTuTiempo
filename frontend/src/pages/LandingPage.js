import React from "react";
import { Link } from "react-router-dom";

const LandingPage = () => {
  return (
    <div style={styles.container}>
      {/* Hero */}
      <section style={styles.hero}>
        <h1 style={styles.heroTitle}>
          Comparte tu tiempo, conecta con tu comunidad
        </h1>
        <p style={styles.heroText}>
          Intercambia habilidades, productos y participa en eventos usando las
          horas como moneda.
        </p>
        <p style = {{ display: "flex", gap: "30px", justifyContent: "center" }}>
          <Link to="/registro" style={styles.heroButton}>
            Empieza ahora
          </Link>
          <Link to="mercado" style={styles.heroButton}>
            Explorar el mercado
          </Link>
        </p>
      </section>

      {/* Características */}
      <section style={styles.features}>
        {[
          {
            title: "Intercambio de habilidades",
            text: "Aprende nuevas destrezas o enseña lo que sabes a otros miembros de la comunidad.",
          },
          {
            title: "Sistema basado en tiempo",
            text: "Las horas son la moneda. Cada servicio o producto se valora con el tiempo invertido.",
          },
          {
            title: "Eventos comunitarios",
            text: "Participa en actividades grupales para compartir, colaborar y fortalecer la comunidad.",
          },
        ].map((item, idx) => (
          <div key={idx} style={styles.card}>
            <h3 style={styles.cardTitle}>{item.title}</h3>
            <p style={styles.cardText}>{item.text}</p>
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
  container: { fontFamily: "Arial, sans-serif", backgroundColor: "#fff", color: "#000" },
  hero: {
    textAlign: "center",
    padding: "140px 20px",
    backgroundColor: "#fff",
  },
  heroTitle: { fontSize: "42px", fontWeight: "bold", marginBottom: "20px", color: "#000" },
  heroText: {
    fontSize: "18px",
    marginBottom: "40px",
    color: "#333",
    maxWidth: "700px",
    margin: "0 auto",
  },
  heroButton: {
    padding: "15px 40px",
    backgroundColor: "#ff6f00",
    color: "#fff",
    fontSize: "18px",
    fontWeight: "bold",
    borderRadius: "8px",
    textDecoration: "none",
    transition: "background 0.3s",
  },
  features: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))",
    gap: "30px",
    padding: "80px 40px",
    backgroundColor: "#f9f9f9",
  },
  card: {
    backgroundColor: "#fff",
    padding: "30px",
    borderRadius: "12px",
    boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
    textAlign: "center",
    transition: "transform 0.2s ease, box-shadow 0.2s ease",
  },
  cardTitle: { fontSize: "20px", fontWeight: "bold", marginBottom: "15px", color: "#000" },
  cardText: { fontSize: "15px", color: "#555" },
  footer: {
    textAlign: "center",
    padding: "30px",
    backgroundColor: "#000",
    color: "#fff",
    marginTop: "0",
  },
};

export default LandingPage;
