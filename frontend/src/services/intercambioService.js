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
    {},
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

export const obtenerSolicitudes = async (token) => {
  const res = await axios.get(`${API_URL}/solicitudes`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return res.data;
};

export const aceptarSolicitud = async (id, token) => {
  const res = await axios.put(
    `${API_URL}/solicitudes/${id}/aceptar`,
    {},
    { headers: { Authorization: `Bearer ${token}` } }
  );
  return res.data;
};

export const rechazarSolicitud = async (id, token) => {
  await axios.put(
    `${API_URL}/solicitudes/${id}/rechazar`,
    {},
    { headers: { Authorization: `Bearer ${token}` } }
  );
};