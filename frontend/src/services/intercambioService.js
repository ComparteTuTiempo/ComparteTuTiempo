import axios from "axios";

const API_URL = "/intercambios";

// 🔹 Obtener todos los intercambios
export const obtenerTodosLosIntercambios = async () => {
  const response = await axios.get(API_URL);
  return response.data;
};

export const obtenerDetalleIntercambioUsuario = async (id, token) => {
  const response = await axios.get(`${API_URL}/intercambiousuario/${id}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const obtenerMisIntercambiosUsuario = async (estado, token) => {
  const response = await axios.get(
    `${API_URL}/estado/${estado}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export async function guardarAcuerdo(intercambioUsuarioId, acuerdo, token) {
  const response = await fetch(
    `${API_URL}/${intercambioUsuarioId}/acuerdo`,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(acuerdo),
    }
  );
  if (!response.ok) {
    throw new Error("Error al guardar acuerdo");
  }
  return response.json();
}

export const finalizarAcuerdo = async (id, token) => {
  const response = await axios.put(
    `${API_URL}/${id}/finalizar`,
    {},
    { headers: { Authorization: `Bearer ${token}` } }
  );
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