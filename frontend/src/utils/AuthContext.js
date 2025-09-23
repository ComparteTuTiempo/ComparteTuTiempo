import { createContext, useContext, useState } from "react";
import { jwtDecode } from "jwt-decode";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const stored = localStorage.getItem("usuario");
  let initialUser = null;
  let initialToken = null;

  if (stored) {
    try {
      const parsed = JSON.parse(stored);
      const jwt = parsed.token;
      initialToken = jwt;

      const decoded = jwtDecode(jwt);
      initialUser = {
        correo: decoded.sub,
        roles: decoded.roles || [],
        ...decoded,
      };
    } catch (err) {
      initialUser = null;
      initialToken = null;
    }
  }

  const [user, setUser] = useState(initialUser);
  const [token, setToken] = useState(initialToken);

  return (
    <AuthContext.Provider value={{ user, token, setUser, setToken }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
