import React, { useState } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import { GoogleLogin } from "@react-oauth/google";
import { jwtDecode } from "jwt-decode";
import FacebookLoginButton from "../components/FacebookLoginButton";
import { FaGoogle } from "react-icons/fa";

const LoginPage = () => {
  const [credenciales, setCredenciales] = useState({ correo: "", contrasena: "" });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredenciales({ ...credenciales, [e.target.name]: e.target.value });
  };

  // 🔹 Login clásico
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const response = await axios.post(`${process.env.REACT_APP_API_URL}/auth/login`, {
        correo: credenciales.correo,
        contraseña: credenciales.contrasena,
      });
      localStorage.setItem("usuario", JSON.stringify({
        token: response.data.token,
        roles: response.data.roles || ["USER"],
      }));
      window.dispatchEvent(new Event("usuario-actualizado"));
      navigate("/");
    } catch (err) {
      setError("Correo o contraseña incorrectos");
    }
  };

  // 🔹 Google login oficial
  const handleGoogleSuccess = async (credentialResponse) => {
    try {
      const decoded = jwtDecode(credentialResponse.credential);
      const response = await axios.post(`${process.env.REACT_APP_API_URL}/api/usuarios/login/google`, {
        correo: decoded.email,
        nombre: decoded.name,
        fotoPerfil: decoded.picture,
        metodoAutenticacion: "GOOGLE",
      });
      localStorage.setItem("usuario", JSON.stringify({
        token: response.data.token,
        roles: response.data.roles || ["USER"],
      }));
      window.dispatchEvent(new Event("usuario-actualizado"));
      navigate("/");
    } catch (err) {
      console.error("❌ Google login error:", err);
      setError("Error al iniciar sesión con Google");
    }
  };

  const handleGoogleError = () => {
    setError("Error al autenticar con Google");
  };

  // 🔹 Facebook login
  const handleFacebookResponse = async (facebookData) => {
    try {
      const response = await axios.post(`${process.env.REACT_APP_API_URL}/api/usuarios/login/facebook`, {
        correo: facebookData.email,
        nombre: facebookData.name,
        fotoPerfil: facebookData.picture?.data?.url,
        metodoAutenticacion: "FACEBOOK",
      });
      localStorage.setItem("usuario", JSON.stringify({
        token: response.data.token,
        roles: response.data.roles || ["USER"],
      }));
      window.dispatchEvent(new Event("usuario-actualizado"));
      navigate("/");
    } catch (err) {
      console.error("❌ Facebook login error:", err);
      setError("Error al iniciar sesión con Facebook");
    }
  };

  return (
    <div style={styles.container}>
      <form onSubmit={handleSubmit} style={styles.form}>
        <h2 style={styles.title}>Log In</h2>

        <label style={styles.label}>Email</label>
        <input
          type="email"
          name="correo"
          value={credenciales.correo}
          onChange={handleChange}
          placeholder="Enter your email"
          style={styles.input}
          required
        />

        <label style={styles.label}>Password</label>
        <input
          type="password"
          name="contrasena"
          value={credenciales.contrasena}
          onChange={handleChange}
          placeholder="Enter your password"
          style={styles.input}
          required
        />

        {error && <p style={styles.error}>{error}</p>}

        <button type="submit" style={styles.button}>
          Log In
        </button>

        <p style={styles.footerText}>
          Don’t have an account?{" "}
          <Link to="/registro" style={styles.link}>
            Register
          </Link>
        </p>

        {/* Google */}
        <div style={{ marginTop: "15px" }}>
          <GoogleLogin
            onSuccess={handleGoogleSuccess}
            onError={handleGoogleError}
            useOneTap
            // 🔹 Para mantener estilo de marca, Google ya ofrece su botón oficial.
            // Si quieres custom, tendrías que usar un wrapper propio.
          />
        </div>

        {/* Facebook */}
        <div style={{ marginTop: "10px" }}>
          <FacebookLoginButton onSuccess={handleFacebookResponse} />
        </div>
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

export default LoginPage;
