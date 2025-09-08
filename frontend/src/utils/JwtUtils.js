import { jwtDecode } from "jwt-decode";

export const getCurrentUserCorreo = () => {
  const token = localStorage.getItem("token");
  if (!token) return null;

  try {
    const decoded = jwtDecode(token);
    return decoded.sub || decoded.subject; 
  } catch (error) {
    console.error("Token inválido", error);
    return null;
  }
};