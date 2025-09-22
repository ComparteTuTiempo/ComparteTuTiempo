import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const LoginPage = () => {
  const [credenciales, setCredenciales] = useState({ correo: "", contrasena: "" });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredenciales({ ...credenciales, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const response = await axios.post("http://localhost:8080/auth/login", {
        correo: credenciales.correo,
        contraseña: credenciales.contrasena
      });

      // Guardar en localStorage con token y roles
      localStorage.setItem("usuario", JSON.stringify({
        token: response.data.token,
        roles: response.data.roles || ["USER"]
      }));

      // Redirigir a landing page
      navigate("/");
    } catch (err) {
      console.error(err);
      setError("Correo o contraseña incorrectos");
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-100">
      <form onSubmit={handleSubmit} className="p-6 bg-white rounded shadow-md w-96 space-y-4">
        <h2 className="text-2xl font-bold">Iniciar sesión</h2>
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
        <button type="submit" className="w-full p-2 bg-indigo-600 text-white rounded">
          Entrar
        </button>
        {error && <p className="text-red-500">{error}</p>}
      </form>
    </div>
  );
};

export default LoginPage;
