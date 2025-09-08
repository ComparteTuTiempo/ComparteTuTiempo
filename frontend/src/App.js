import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import LandingPage from "./pages/LandingPage";
import RegistroUsuario from "./pages/RegistroUsuario";
import LoginPage from "./pages/LoginPage";
import UserProfile from "./pages/ProfileView";
import { getCurrentUserCorreo } from "./utils/JwtUtils";
import { AuthProvider } from "./utils/AuthContext";

function App() {
  

  const correo = getCurrentUserCorreo();
  
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Todas las páginas usan Layout */}
          <Route element={<Layout />}>
            <Route path="/" element={<LandingPage />} />
            <Route path="/registro" element={<RegistroUsuario />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/profile" element={<UserProfile correo={correo} />}
          />
          </Route>
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
