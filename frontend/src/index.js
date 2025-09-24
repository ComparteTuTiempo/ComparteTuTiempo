import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { GoogleOAuthProvider } from '@react-oauth/google';

// ⚠️ Reemplaza esto por tu verdadero CLIENT_ID de Google
const clientId = "801826047633-1bk1mb42jccek2r8co8ha2fcvmsm5gem.apps.googleusercontent.com";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <GoogleOAuthProvider clientId={clientId}>
      <App />
    </GoogleOAuthProvider>
  </React.StrictMode>
);

// Si quieres medir el rendimiento de tu app
reportWebVitals();
