import React, { useState } from "react";
import RegistroUsuarioForm from "../components/RegistroUsuarioForm";
import Layout from "../components/Layout";
import axios from "axios";

const RegistroUsuario = () => {
  const [usuario, setUsuario] = useState({
    nombre: "",
    correo: "",
    contrasena: "",
    fechaNacimiento: "",
    biografia: "",
    fotoPerfil: "",
    ubicacion: "",
    metodoAutenticacion: "correo",
  });

  const [errores, setErrores] = useState({});

  const handleChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "fotoPerfil" && files && files[0]) {
      // Subida de imagen local
      const reader = new FileReader();
      reader.onloadend = () => {
        setUsuario({ ...usuario, fotoPerfil: reader.result });
      };
      reader.readAsDataURL(files[0]);
    } else {
      setUsuario({ ...usuario, [name]: value });
    }
  };

  const validar = () => {
    const errores = {};

    // Contraseña: 1 mayúscula, 1 número, al menos 10 caracteres
    const passRegex = /^(?=.*[A-Z])(?=.*\d).{10,}$/;
    if (!passRegex.test(usuario.contrasena)) {
      errores.contrasena =
        "La contraseña debe tener al menos 10 caracteres, 1 mayúscula y 1 número";
    }

    // Foto de perfil: URL válida si no es local
    if (usuario.fotoPerfil && !usuario.fotoPerfil.startsWith("data:")) {
      try {
        new URL(usuario.fotoPerfil);
      } catch {
        errores.fotoPerfil = "La foto debe ser una URL válida o un archivo local";
      }
    }

    setErrores(errores);
    return Object.keys(errores).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validar()) return;

    try {
      await axios.post("http://localhost:8080/api/usuarios", usuario);
      alert("Usuario registrado con éxito 🚀");
      setUsuario({
        nombre: "",
        correo: "",
        contrasena: "",
        fechaNacimiento: "",
        biografia: "",
        fotoPerfil: "",
        ubicacion: "",
        metodoAutenticacion: "correo",
      });
      setErrores({});
    } catch (error) {
      console.error(error);
      alert("Error al registrar usuario");
    }
  };

  return (
    <Layout>
      <RegistroUsuarioForm
        usuario={usuario}
        onChange={handleChange}
        onSubmit={handleSubmit}
        errores={errores}
      />
    </Layout>
  );
};

export default RegistroUsuario;
