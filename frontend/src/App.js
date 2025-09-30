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


function App() {
  return (
      <AuthProvider>
        <WebSocketProvider>
          <Router>
            <Routes>
              {/* Todas las páginas usan Layout */}
                <Route element={<Layout />}>
                <Route path="/" element={<LandingPage />} />
                <Route path="/registro" element={<RegistroUsuarioForm />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/perfil" element={<UserProfile/>}/>
                <Route path="/perfil/:correo" element={<UserProfile />} />
                <Route path="/crear-oferta" element={<CrearOferta />} />
                <Route path="/intercambios/:id/editar" element={<CrearOferta />} />
                <Route path="/conversaciones" element={<ConversacionList />} />
                <Route path="/conversaciones/:id" element={<ConversacionView />} />
                <Route path="/eventos/crear" element={<CrearEventoForm  />} />
                <Route path="/eventos/:id" element={<EventoDetails  />} />
                <Route path="/eventos/:id/participantes/lista" element={<ListaAsistencia  />} />                    
                <Route path="/intercambios" element={<IntercambiosPage />} />
                <Route path="/mercado" element={<MarketPage />} />
                <Route path="/producto/nuevo" element={<ProductoPage />} />
                <Route path="/mispublicaciones" element={<PublicacionesPage />} />
                <Route path="/productos/editar/:id" element={<ProductoPage />} />
                <Route path="/intercambio/:id" element={<IntercambioDetails />} />
                <Route path="/solicitudes" element={<SolicitudesIntercambio />} />
                <Route path="/intercambios/usuario" element={<IntercambiosPorEstado />} />
                <Route path="/acuerdos/:id" element={<FormularioAcuerdo />} />
                <Route path="/producto/:id" element={<DetalleProducto />} />
                <Route path="/productousuario/transacciones" element={<ProductoUsuarioDetails />} />
                <Route path="/notificaciones" element={<NotificacionesPage />} />

                <Route element={<Layout />}>

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
              </Route>
            </Routes>
          </Router>
        </WebSocketProvider>
      </AuthProvider>
  );
}

export default App;
