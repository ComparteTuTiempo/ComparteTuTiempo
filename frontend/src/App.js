import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import ProtectedRoute from "./utils/ProtectedRoute";
import LandingPage from "./pages/LandingPage";
import RegistroUsuarioForm from "./forms/RegistroUsuarioForm";
import LoginPage from "./pages/LoginPage";
import CrearOferta from "./forms/IntercambioForm";
import UserProfile from "./pages/ProfileView";
import { WebSocketProvider } from "./utils/WebSocketProvider";
import { AuthProvider } from "./utils/AuthContext";
import ConversacionList from "./pages/ConversacionList";
import ConversacionView from "./pages/ConversacionView";
import CrearEventoForm from "./forms/CrearEventoForm";
import EventoDetails from "./pages/EventoDetails";
import ListaAsistencia from "./pages/AsistenciaList";
import VerificacionForm from "./forms/VerificacionForm";
import AdminVerificationPage from "./pages/AdminVerificationPage";
import PublicRoute from "./utils/PublicRoute";

import IntercambiosPage from "./pages/IntercambiosPage";
import MarketPage from "./pages/MarketPage";
import ProductoPage from "./pages/ProductoPage";
import PublicacionesPage from "./pages/PublicacionesPage";

import IntercambioDetails from "./pages/IntercambioDetails";
import SolicitudesIntercambio from "./pages/SolicitudesIntercambioList";
import IntercambiosPorEstado from "./pages/IntercambiosByEstado";
import FormularioAcuerdo from "./forms/AcuerdoForm";

import HistorialPage from "./pages/HistorialPage";
import BuscarUsuarioPage from "./pages/BuscarUsuarioPage";
import ReportesPage from "./pages/ReportesPage";
import CategoriaForm from "./forms/CategoriaForm";

import DetalleProducto from "./pages/ProductoDetails";
import ProductoUsuarioDetails from "./pages/ProductoUsuarioDetails";
import NotificacionesPage from "./pages/NotificacionesPage";

import EventosPage from "./pages/EventosPage";
import InicioPage from "./pages/InicioPage";


function App() {
  return (
      <AuthProvider>
        <WebSocketProvider>
          <Router>
            <Routes>
              {/* Todas las páginas usan Layout */}
                <Route element={<Layout />}>
                <Route path="/" element={<LandingPage />} />
                <Route path="/registro" element={<PublicRoute><RegistroUsuarioForm /></PublicRoute>} />
                <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
                <Route path="/perfil" element={<ProtectedRoute><UserProfile/></ProtectedRoute>}/>
                <Route path="/perfil/:correo" element={<ProtectedRoute><UserProfile /></ProtectedRoute>} />
                <Route path="/crear-oferta" element={<ProtectedRoute><CrearOferta /></ProtectedRoute>} />
                <Route path="/intercambios/:id/editar" element={<ProtectedRoute><CrearOferta /></ProtectedRoute>} />
                <Route path="/conversaciones" element={<ProtectedRoute><ConversacionList /></ProtectedRoute>} />
                <Route path="/conversaciones/:id" element={<ProtectedRoute><ConversacionView /></ProtectedRoute>} />
                <Route path="/eventos/crear" element={<ProtectedRoute><CrearEventoForm  /></ProtectedRoute>} />
                <Route path="/eventos/:id" element={<ProtectedRoute><EventoDetails  /></ProtectedRoute>} />
                <Route path="/eventos/:id/participantes/lista" element={<ProtectedRoute><ListaAsistencia  /></ProtectedRoute>} />                    
                <Route path="/mercado" element={<MarketPage />} />
                <Route path="/producto/nuevo" element={<ProtectedRoute><ProductoPage /></ProtectedRoute>} />
                <Route path="/mispublicaciones" element={<ProtectedRoute><PublicacionesPage /></ProtectedRoute>} />
                <Route path="/productos/editar/:id" element={<ProtectedRoute><ProductoPage /></ProtectedRoute>} />
                <Route path="/intercambio/:id" element={<ProtectedRoute><IntercambioDetails /></ProtectedRoute>} />
                <Route path="/solicitudes" element={<ProtectedRoute><SolicitudesIntercambio /></ProtectedRoute>} />
                <Route path="/intercambios/usuario" element={<ProtectedRoute><IntercambiosPorEstado /></ProtectedRoute>} />
                <Route path="/acuerdos/:id" element={<ProtectedRoute><FormularioAcuerdo /></ProtectedRoute>} />
                <Route path="/producto/:id" element={<ProtectedRoute><DetalleProducto /></ProtectedRoute>} />
                <Route path="/productousuario/transacciones" element={<ProtectedRoute><ProductoUsuarioDetails /></ProtectedRoute>} />
                <Route path="/notificaciones" element={<ProtectedRoute><NotificacionesPage /></ProtectedRoute>} />
                <Route path="/eventos" element={<ProtectedRoute><EventosPage /></ProtectedRoute>} />
                <Route path="/inicio" element={<ProtectedRoute><InicioPage/></ProtectedRoute>} />

                {/* Ruta protegida: usuario normal */}
                <Route
                  path="/verificacion"
                  element={
                    <ProtectedRoute roles={["USER"]}>
                      <VerificacionForm />
                    </ProtectedRoute>
                  }
                />

                {/* Ruta protegida: administrador */}
                <Route
                  path="/admin/verificaciones"
                  element={
                    <ProtectedRoute roles={["ADMIN"]}>
                      <AdminVerificationPage />
                    </ProtectedRoute>
                  }
                />
                <Route path="/historial" element={<HistorialPage />} />
                <Route path="/buscarusuarios" element={<BuscarUsuarioPage />} />

                <Route
                  path="/admin/reportes"
                  element={
                    <ProtectedRoute roles={["ADMIN"]}>
                      <ReportesPage />
                    </ProtectedRoute>
                  }
                />

                  <Route
                  path="/admin/categorias"
                  element={
                    <ProtectedRoute roles={["ADMIN"]}>
                      <CategoriaForm />
                    </ProtectedRoute>
                  }
                />
              </Route>
            </Routes>
          </Router>
        </WebSocketProvider>
      </AuthProvider>
  );
}

export default App;
