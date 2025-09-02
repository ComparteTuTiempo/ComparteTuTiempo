import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import LandingPage from "./pages/LandingPage";
import RegistroUsuario from "./pages/RegistroUsuario";
import LoginPage from "./pages/LoginPage";

function App() {
  return (
    <Router>
      <Routes>
        {/* Todas las páginas usan Layout */}
        <Route element={<Layout />}>
          <Route path="/" element={<LandingPage />} />
          <Route path="/registro" element={<RegistroUsuario />} />
          <Route path="/login" element={<LoginPage />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
