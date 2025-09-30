import axios from "axios";

const API_URL = "/conversaciones";

const getAuthHeader = (token) => {
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const getConversacionIntercambio = async (intercambioId,token) => {
  const response = await fetch(
      `${API_URL}/por-intercambio/${intercambioId}`,
      {
        headers: getAuthHeader(token),
      }
    );
  return response.data;
};

