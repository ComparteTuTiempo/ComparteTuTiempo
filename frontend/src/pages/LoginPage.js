import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { GoogleLogin } from "@react-oauth/google";
import { jwtDecode } from "jwt-decode";
import FacebookLoginButton from "../components/FacebookLoginButton";

const LoginPage = () => {
  const [credenciales, setCredenciales] = useState({ correo: "", contrasena: "" });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  // ---- Login clásico ----
  const handleChange = (e) => {
    setCredenciales({ ...credenciales, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const response = await axios.post("http://localhost:8080/api/usuarios/login", {
        correo: credenciales.correo,
        contrasena: credenciales.contrasena,
      });

      localStorage.setItem(
        "usuario",
        JSON.stringify({
          token: response.data.token,
          roles: response.data.roles || ["USER"],
        })
      );

      navigate("/");
    } catch (err) {
      if (err.response) {
        if (err.response.status === 403) {
          setError("Este usuario ha sido baneado ❌");
        } else if (err.response.status === 401) {
          setError("Correo o contraseña incorrectos");
        } else {
          setError("Error en el servidor, inténtalo más tarde");
        }
      } else {
        setError("No se pudo conectar con el servidor");
      }
    }
  };

  // ---- Login con Google ----
  const handleGoogleSuccess = async (credentialResponse) => {
    try {
      const decoded = jwtDecode(credentialResponse.credential);

      const response = await axios.post("http://localhost:8080/api/usuarios/login/google", {
        correo: decoded.email,
        nombre: decoded.name,
        fotoPerfil: decoded.picture,
        metodoAutenticacion: "GOOGLE",
      });

      localStorage.setItem(
        "usuario",
        JSON.stringify({
          token: response.data.token,
          roles: response.data.roles || ["USER"],
        })
      );

      navigate("/");
    } catch (err) {
      console.error("❌ Google login error:", err);
      setError("Error al iniciar sesión con Google");
    }
  };

  const handleGoogleError = () => {
    setError("Error al autenticar con Google");
  };

  // ---- Login con Facebook ----
  const handleFacebookResponse = async (facebookData) => {
    try {
      const response = await axios.post("http://localhost:8080/api/usuarios/login/facebook", {
        correo: facebookData.email,
        nombre: facebookData.name,
        fotoPerfil: facebookData.picture?.data?.url,
        metodoAutenticacion: "FACEBOOK",
      });

      localStorage.setItem(
        "usuario",
        JSON.stringify({
          token: response.data.token,
          roles: response.data.roles || ["USER"],
        })
      );

      navigate("/");
    } catch (err) {
      console.error("❌ Facebook login error:", err);
      setError("Error al iniciar sesión con Facebook");
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-100">
      <form
        onSubmit={handleSubmit}
        className="p-6 bg-white rounded shadow-md w-96 space-y-4"
      >
        <h2 className="text-2xl font-bold">Iniciar sesión</h2>

        {/* Login normal */}
        <input
          type="email"
          name="correo"
          placeholder="Correo"
          value={credenciales.correo}
          onChange={handleChange}
          className="w-full p-2 border rounded"
          required
        />
        <input
          type="password"
          name="contrasena"
          placeholder="Contraseña"
          value={credenciales.contrasena}
          onChange={handleChange}
          className="w-full p-2 border rounded"
          required
        />
        <button
          type="submit"
          className="w-full p-2 bg-indigo-600 text-white rounded"
        >
          Entrar
        </button>

        {/* Google */}
        <div className="w-full flex justify-center mt-4">
          <GoogleLogin onSuccess={handleGoogleSuccess} onError={handleGoogleError} />
        </div>

        {/* Facebook */}
        <div className="w-full flex justify-center mt-4">
          <FacebookLoginButton onSuccess={handleFacebookResponse} />
        </div>

        {error && <p className="text-red-500">{error}</p>}
      </form>
    </div>
  );
};

export default LoginPage;
