import axios from "axios";

const API_URL = "/eventos";

const getAuthHeader = (token) => {
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const listarEventos = async () => {
  const response = await axios.get(API_URL);
  return response.data;
};

export const listarMisParticipaciones = async (correo, token) => {
  const response = await axios.get(`${API_URL}/mis-participaciones`, {
    params: { correo },
    headers: getAuthHeader(token),
  });
  return response.data;
};

export const getEventoById = async (id) => {
  const response = await axios.get(`${API_URL}/${id}`);
  return response.data;
};

export const crearEvento = async (eventoData, token) => {
  const response = await axios.post(`${API_URL}/crear`, eventoData, {
    headers: getAuthHeader(token),
  });
  return response.data;
};

export const registrarParticipacion = async (eventoId, correoUsuario, token) => {
  try {
    const response = await axios.post(
      `${API_URL}/${eventoId}/participar/${correoUsuario}`,
      null,
      { headers: getAuthHeader(token) }
    );
    return response.data;
  } catch (error) {
    if (error.response && error.response.data) {
      alert(error.response.data);
    } else {
      alert("Error al unirse al evento.");
    }
    throw error;
  }
};

export const getParticipantesEventoById = async (id) => {
  const response = await axios.get(`${API_URL}/${id}/participantes`);
  return response.data;
};

export const cargarParticipantes = async (eventoId, correoOrganizador, token) => {
  const response = await axios.get(`${API_URL}/${eventoId}/participantes/lista`, {
    params: { correoOrganizador },
    headers: getAuthHeader(token),
  });
  return response.data;
};

export const marcarAsistencia = async (eventoId, correoOrganizador, correoParticipante, asistio, token) => {
  const response = await axios.post(
    `${API_URL}/${eventoId}/asistencia`,
    null,
    {
      params: { correoOrganizador, correoParticipante, asistio },
      headers: getAuthHeader(token),
    }
  );
  return response.data;
};

export const finalizarEvento = async (eventoId, correoOrganizador, token) => {
  const response = await axios.post(
    `${API_URL}/${eventoId}/finalizar`,
    null,
    {
      params: { correoOrganizador },
      headers: getAuthHeader(token),
    }
  );
  return response.data;
};
