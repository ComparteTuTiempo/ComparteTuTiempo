import axios from 'axios';

const API_URL = "/eventos";

export const getEventoById = async (id) => {
  const response = await axios.get(`${API_URL}/${id}`);
  return response.data;
};

export const getParticipantesEventoById = async (id) => {
  const response = await axios.get(`${API_URL}/${id}/participantes`);
  return response.data;
};

export const registrarParticipacion = async (id, correoUsuario) => {
  try {
    const response = await axios.post(`/eventos/${id}/participar/${correoUsuario}`);
    console.log(response);
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

export const crearEvento = async (eventoData) => {
  const response = await axios.post(`${API_URL}/crear`, eventoData);
  return response.data;
};

export const listarEventos = async () => {
  const response = await axios.get(API_URL);
  return response.data;
};