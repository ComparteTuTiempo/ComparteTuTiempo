import axios from "axios";

const API_URL = `${process.env.REACT_APP_API_URL}/notificaciones`;

const getAuthHeader = (token) => {
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const getNotificaciones = async (token) => {
  const response = await axios.get(API_URL, {
    headers: getAuthHeader(token),
  });
  return response.data;
};

export const marcarTodasComoLeidas = async (token) => {
  const response = await axios.put(
    `${API_URL}/leer-todas`,
    null,
    { headers: getAuthHeader(token) }
  );
  return response.data;
};