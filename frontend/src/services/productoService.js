import axios from "axios";

const API_URL = `http://localhost:8080/productos`;


export const obtenerProductos = async () => {
  const response = await axios.get(API_URL);
  return response.data;
};


export const obtenerProductoPorId = async (id) => {
  const response = await axios.get(`${API_URL}/${id}`);
  return response.data;
};


export const obtenerMisProductos = async (token) => {
  const response = await axios.get(`${API_URL}/usuario`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return response.data;
};


export const crearProducto = async (producto, token) => {
  const response = await axios.post(API_URL, producto, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return response.data;
};


export const actualizarProducto = async (id, producto, token) => {
  const response = await axios.put(`${API_URL}/${id}`, producto, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return response.data;
};


export const eliminarProducto = async (id, token) => {
  await axios.delete(`${API_URL}/${id}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
};

export const obtenerHistorialProductos = async (token) => {
  const response = await axios.get(`${API_URL}/historial`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return response.data;
};

export const adquirirProducto = async (id, token) => {
  const response = await axios.post(
    `${API_URL}/${id}/adquirir`,
    {},
    { headers: { Authorization: `Bearer ${token}` } }
  );
  return response.data;
};
