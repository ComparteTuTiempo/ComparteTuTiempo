import axios from "axios";

const API_URL = "/productousuario";

export const obtenerMisTransacciones = async (token) => {
  const res = await axios.get(`${API_URL}/transacciones`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return res.data;
};

export const adquirirProducto = async (productoId, token) => {
  const res = await axios.post(
    `${API_URL}/adquirir/${productoId}`,
    {},
    { headers: { Authorization: `Bearer ${token}` } }
  );
  return res.data;
};

export const finalizarTransaccion = async (transaccionId, token) => {
  const res = await axios.put(
    `${API_URL}/finalizar/${transaccionId}`,
    {},
    { headers: { Authorization: `Bearer ${token}` } }
  );
  return res.data;
};
