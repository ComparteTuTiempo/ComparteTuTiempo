import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../utils/AuthContext";
import { guardarAcuerdo } from "../services/intercambioService";

const FormularioAcuerdo = () => {
  const { id } = useParams();
  const { token } = useAuth();
  const navigate = useNavigate();

  const [horasAsignadas, setHorasAsignadas] = useState("");
  const [terminos, setTerminos] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await guardarAcuerdo(id, { horasAsignadas, terminos }, token);
      alert("Acuerdo guardado correctamente ✅");
      navigate("/intercambios/usuario"); 
    } catch (error) {
      console.error("Error al guardar acuerdo:", error);
      alert("Hubo un error al guardar el acuerdo");
    }
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>Establecer Acuerdo</h2>
      <form
        onSubmit={handleSubmit}
        style={{
          display: "flex",
          flexDirection: "column",
          gap: "15px",
          maxWidth: "400px",
        }}
      >
        <label>
          Número de horas:
          <input
            type="number"
            value={horasAsignadas}
            onChange={(e) => setHorasAsignadas(e.target.value)}
            required
            style={{ width: "100%", padding: "8px" }}
          />
        </label>

        <label>
          Términos del acuerdo:
          <textarea
            value={terminos}
            onChange={(e) => setTerminos(e.target.value)}
            required
            rows="4"
            style={{ width: "100%", padding: "8px" }}
          />
        </label>

        <button
          type="submit"
          style={{
            padding: "10px 14px",
            backgroundColor: "#007bff",
            color: "white",
            border: "none",
            borderRadius: "5px",
            cursor: "pointer",
          }}
        >
          Guardar acuerdo
        </button>
      </form>
    </div>
  );
};

export default FormularioAcuerdo;
