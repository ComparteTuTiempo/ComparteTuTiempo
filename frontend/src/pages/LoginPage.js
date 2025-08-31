import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import LoginForm from "../components/LoginForm";

const LoginPage = () => {
  const [credenciales, setCredenciales] = useState({
    correo: "",
    contrasena: "",
  });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredenciales({ ...credenciales, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const response = await axios.post(
        "http://localhost:8080/api/usuarios/login",
        credenciales
      );

      // Guardar usuario en localStorage
      localStorage.setItem("usuario", JSON.stringify(response.data));

      // Redirigir a landing page
      navigate("/");
    } catch (err) {
      setError("Correo o contraseña incorrectos");
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-100">
      <LoginForm
        credenciales={credenciales}
        error={error}
        onChange={handleChange}
        onSubmit={handleSubmit}
      />
    </div>
  );
};

export default LoginPage;
