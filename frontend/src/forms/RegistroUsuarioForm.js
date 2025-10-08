import React, { useState } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";

const RegistroUsuario = () => {
  const navigate = useNavigate();

  const [usuario, setUsuario] = useState({
    nombre: "",
    correo: "",
    contrasena: "",
    confirmarContrasena: "",
  });

  const [errores, setErrores] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUsuario({ ...usuario, [name]: value });
  };

  const validar = () => {
    const errores = {};

    if (usuario.contrasena !== usuario.confirmarContrasena) {
      errores.confirmarContrasena = "Las contraseñas no coinciden";
    }

    // Contraseña segura: al menos 1 mayúscula, 1 número, 10 caracteres
    const passRegex = /^(?=.*[A-Z])(?=.*\d).{10,}$/;
    if (!passRegex.test(usuario.contrasena)) {
      errores.contrasena =
        "La contraseña debe tener al menos 10 caracteres, 1 mayúscula y 1 número";
    }

    setErrores(errores);
    return Object.keys(errores).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validar()) return;

    try {
      await axios.post(`http://localhost:8080/api/usuarios`, {
        nombre: usuario.nombre,
        correo: usuario.correo,
        contrasena: usuario.contrasena,
      });
      alert("Usuario registrado con éxito 🚀");
      navigate("/login");
    } catch (error) {
      console.error(error);
      alert("Error al registrar usuario");
    }
  };

  return (
    <div style={styles.container}>
      <form onSubmit={handleSubmit} style={styles.form}>
        <h2 style={styles.title}>Registrarse</h2>

        <label style={styles.label}>Nombre</label>
        <input
          type="text"
          name="nombre"
          value={usuario.nombre}
          onChange={handleChange}
          placeholder="Introduce tu nombre"
          style={styles.input}
          required
        />

        <label style={styles.label}>Email</label>
        <input
          type="email"
          name="correo"
          value={usuario.correo}
          onChange={handleChange}
          placeholder="Enter your email"
          style={styles.input}
          required
        />

        <label style={styles.label}>Contraseña</label>
        <input
          type="password"
          name="contrasena"
          value={usuario.contrasena}
          onChange={handleChange}
          placeholder="Contraseña"
          style={styles.input}
          required
        />
        {errores.contrasena && (
          <p style={styles.error}>{errores.contrasena}</p>
        )}

        <label style={styles.label}>Confirmar Contraseña</label>
        <input
          type="password"
          name="confirmarContrasena"
          value={usuario.confirmarContrasena}
          onChange={handleChange}
          placeholder="Confirmar Contraseña"
          style={styles.input}
          required
        />
        {errores.confirmarContrasena && (
          <p style={styles.error}>{errores.confirmarContrasena}</p>
        )}

        <button type="submit" style={styles.button}>
          Registrarse
        </button>

        <p style={styles.footerText}>
          ¿Ya tienes una cuenta?{" "}
          <Link to="/login" style={styles.link}>
            Log In
          </Link>
        </p>
      </form>
    </div>
  );
};

const styles = {
  container: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    minHeight: "80vh",
    backgroundColor: "#f9f9f9",
  },
  form: {
    backgroundColor: "#fff",
    padding: "40px",
    borderRadius: "12px",
    boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
    width: "100%",
    maxWidth: "400px",
    textAlign: "left",
  },
  title: {
    fontSize: "24px",
    fontWeight: "bold",
    marginBottom: "20px",
    textAlign: "center",
  },
  label: { fontSize: "14px", fontWeight: "bold", marginBottom: "6px" },
  input: {
    width: "100%",
    padding: "10px",
    marginBottom: "15px",
    borderRadius: "6px",
    border: "1px solid #ccc",
    fontSize: "14px",
  },
  button: {
    width: "100%",
    padding: "12px",
    border: "none",
    borderRadius: "6px",
    backgroundColor: "#ff6f00",
    color: "#fff",
    fontSize: "16px",
    fontWeight: "bold",
    cursor: "pointer",
  },
  footerText: {
    marginTop: "15px",
    fontSize: "14px",
    textAlign: "center",
  },
  link: { color: "#ff6f00", textDecoration: "none", fontWeight: "bold" },
  error: { color: "red", fontSize: "12px", marginBottom: "10px" },
};

export default RegistroUsuario;
