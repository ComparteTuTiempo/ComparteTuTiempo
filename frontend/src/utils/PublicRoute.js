// src/utils/PublicRoute.js
import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

const PublicRoute = ({ children }) => {
  const { user } = useAuth();

  // 👤 Si el usuario ya está logueado, no tiene sentido que vea login o registro
  if (user) {
    // Redirige al home o perfil (puedes cambiar la ruta destino)
    return <Navigate to="/inicio" replace />;
  }

  // 🚪 Si no está logueado, puede acceder normalmente
  return children;
};

export default PublicRoute;
