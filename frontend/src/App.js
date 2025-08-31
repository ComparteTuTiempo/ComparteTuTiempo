import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import LandingPage from "./pages/LandingPage";
import RegistroUsuario from "./pages/RegistroUsuario";

function App() {
  return (
    <Router>
      <Routes>
        {/* Todas las páginas usan Layout */}
        <Route element={<Layout />}>
          <Route path="/" element={<LandingPage />} />
          <Route path="/registro" element={<RegistroUsuario />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
