import axios from "axios";

const API_URL = "/notificaciones";

const getAuthHeader = (token) => {
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const getNotificaciones = async (token) => {
  const response = await axios.get(API_URL, {
    headers: getAuthHeader(token),
  });
  return response.data;
};

export const marcarComoLeida = async (id, token) => {
  const response = await axios.post(
    `${API_URL}/${id}/leida`,
    null,
    { headers: getAuthHeader(token) }
  );
  return response.data;
};

export const marcarTodasComoLeidas = async (token) => {
  const response = await axios.post(
    `${API_URL}/leidas`,
    null,
    { headers: getAuthHeader(token) }
  );
  return response.data;
};