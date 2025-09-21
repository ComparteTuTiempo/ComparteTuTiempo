import axios from "axios";

const API_URL = "/intercambios";

// 🔹 Obtener todos los intercambios
export const obtenerTodosLosIntercambios = async () => {
  const response = await axios.get(API_URL);
  return response.data;
};

// 🔹 Obtener un intercambio por ID
export const obtenerIntercambioPorId = async (id, token) => {
  const response = await axios.get(`${API_URL}/${id}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

// 🔹 Solicitar un intercambio (un usuario pide al ofertante)
export const solicitarIntercambio = async (intercambioId, token) => {
  const response = await axios.post(
    `${API_URL}/${intercambioId}/solicitar`,
    {}, // no mandamos body, solo token
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

// 🔹 Avanzar estado de un intercambio
export const avanzarIntercambio = async (intercambioId, token) => {
  const response = await axios.put(
    `${API_URL}/${intercambioId}/avanzar`,
    {},
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

// 🔹 Obtener intercambios de un usuario autenticado
export const obtenerMisIntercambios = async (token) => {
  const response = await axios.get(`${API_URL}/usuario`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};
