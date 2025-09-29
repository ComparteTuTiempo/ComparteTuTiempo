import axios from "axios";

const API_URL = "/notificaciones";

const headers = () => ({
  Authorization: `Bearer ${localStorage.getItem("token")}`,
});

export const getNotificaciones = () => {
  return axios.get(API_URL, { headers: headers() });
};

export const marcarComoLeida = (id) => {
  return axios.put(`${API_URL}/${id}/leer`, {}, { headers: headers() });
};

export const marcarTodasComoLeidas = () => {
  return axios.put(`${API_URL}/leer-todas`, {}, { headers: headers() });
};