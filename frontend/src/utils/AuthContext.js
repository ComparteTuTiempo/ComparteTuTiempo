import { createContext, useContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);

  useEffect(() => {
    const stored = localStorage.getItem("usuario"); 
    if (stored) {
      try {
        const parsed = JSON.parse(stored);
        const jwt = parsed.token; 

        const decoded = jwtDecode(jwt);
        console.log("🔑 Token decodificado:", decoded);

        setToken(jwt);
        setUser({
          correo: decoded.sub,   
          rol: decoded.scope,    
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

