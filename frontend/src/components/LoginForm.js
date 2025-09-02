import React from "react";

const LoginForm = ({ credenciales, error, onChange, onSubmit }) => {
  return (
    <div className="max-w-md mx-auto text-center bg-white p-6 shadow rounded-2xl">
      <h2 className="text-2xl font-bold mb-4 text-orange-500">Iniciar Sesión</h2>

      <form onSubmit={onSubmit} className="space-y-4">
        <input
          type="email"
          name="correo"
          placeholder="Correo electrónico"
          value={credenciales.correo}
          onChange={onChange}
          required
          className="w-full border border-gray-300 p-2 rounded"
        />

        <input
          type="password"
          name="contrasena"
          placeholder="Contraseña"
          value={credenciales.contrasena}
          onChange={onChange}
          required
          className="w-full border border-gray-300 p-2 rounded"
        />

        {error && <p className="text-red-500">{error}</p>}

        <button
          type="submit"
          className="bg-orange-500 hover:bg-orange-600 text-white w-full py-2 rounded font-semibold"
        >
          Entrar
        </button>
      </form>
    </div>
  );
};

export default LoginForm;
