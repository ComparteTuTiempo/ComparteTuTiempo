// src/utils/ProtectedRoute.js
import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

const ProtectedRoute = ({ children, roles }) => {
  const { user } = useAuth();

  // Si todavía no se ha cargado el usuario desde localStorage, mostramos null o un loader
  console.log("ProtectedRoute - user:", user);
  if (user === null) {
    return <Navigate to="/login" replace />; // o un spinner: <div>Cargando...</div>
  }

  // Si no hay usuario logueado, redirigimos al login
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // Si hay roles permitidos y el usuario no tiene ninguno de ellos, redirigimos a la home
  if (roles && !roles.some((r) => user.roles.includes(r))) {
    return <Navigate to="/" replace />;
  }

  // Usuario logueado y con rol permitido: renderizamos los hijos
  return children;
};

export default ProtectedRoute;
