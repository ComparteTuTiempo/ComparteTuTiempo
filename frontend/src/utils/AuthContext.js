import { createContext, useContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);

  useEffect(() => {
    const stored = localStorage.getItem("usuario"); // aquí tienes el JSON
    if (stored) {
      try {
        // 👇 parseamos el JSON { token: "..." }
        const parsed = JSON.parse(stored);
        const jwt = parsed.token; // extraemos el string JWT

        const decoded = jwtDecode(jwt);
        console.log("🔑 Token decodificado:", decoded);

        setToken(jwt);
        setUser({
          correo: decoded.sub,   // subject es el correo
          rol: decoded.scope,    // claim extra si lo usas
          ...decoded,
        });
      } catch (err) {
        console.error("❌ Error al procesar token", err);
        setUser(null);
        setToken(null);
      }
    }
  }, []);

  return (
    <AuthContext.Provider value={{ user, token }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

export const useUserFromToken = () => {
  const { user } = useAuth();
  return user;
};

