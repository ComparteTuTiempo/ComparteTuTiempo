import { createContext, useContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const getStoredUser = () => {
    const stored = localStorage.getItem("usuario");
    if (!stored) return { user: null, token: null };

    try {
      const parsed = JSON.parse(stored);
      const jwt = parsed.token;
      const decoded = jwtDecode(jwt);

      return {
        user: {
          correo: decoded.sub,
          roles: decoded.roles || [],
          ...decoded,
        },
        token: jwt,
      };
    } catch (err) {
      return { user: null, token: null };
    }
  };

  const { user: initialUser, token: initialToken } = getStoredUser();
  const [user, setUser] = useState(initialUser);
  const [token, setToken] = useState(initialToken);

  useEffect(() => {
    const handleUpdate = () => {
      const { user, token } = getStoredUser();
      setUser(user);
      setToken(token);
    };

    window.addEventListener("usuario-actualizado", handleUpdate);
    window.addEventListener("storage", handleUpdate);

    return () => {
      window.removeEventListener("usuario-actualizado", handleUpdate);
      window.removeEventListener("storage", handleUpdate);
    };
  }, []);

  return (
    <AuthContext.Provider value={{ user, token, setUser, setToken }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
