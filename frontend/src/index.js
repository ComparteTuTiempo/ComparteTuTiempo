import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { GoogleOAuthProvider } from '@react-oauth/google';

// ⚠️ Reemplaza esto por tu verdadero CLIENT_ID de Google
const clientId = "137931658749-225jt7qpcmrvfnm3ui60lsviq785alrp.apps.googleusercontent.com";

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
