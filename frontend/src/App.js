import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import LandingPage from "./pages/LandingPage";
import RegistroUsuario from "./pages/RegistroUsuario";
import LoginPage from "./pages/LoginPage";
import CrearOferta from "./forms/IntercambioForm";
import UserProfile from "./pages/ProfileView";
import { getCurrentUserCorreo } from "./utils/JwtUtils";
import { WebSocketProvider } from "./utils/WebSocketProvider";
import { AuthProvider } from "./utils/AuthContext";
import ConversacionList from "./pages/ConversacionList";
import ConversacionView from "./pages/ConversacionView";


function App() {
  

  const correo = getCurrentUserCorreo();
  
  return (
    
      <AuthProvider>
        <WebSocketProvider>
          <Router>
            <Routes>
              {/* Todas las páginas usan Layout */}
              <Route element={<Layout />}>
                <Route path="/" element={<LandingPage />} />
                <Route path="/registro" element={<RegistroUsuario />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/perfil" element={<UserProfile correo={correo} />}/>
                <Route path="/perfil/:correo" element={<UserProfile />} />
                <Route path="/crear-oferta" element={<CrearOferta />} />
                <Route path="/intercambios/:id/editar" element={<CrearOferta />} />
                <Route path="/conversaciones" element={<ConversacionList />} />
                <Route path="/conversaciones/:id" element={<ConversacionView />} />           
              </Route>
            </Routes>
          </Router>
        </WebSocketProvider>
      </AuthProvider>
  );
}

export default App;
