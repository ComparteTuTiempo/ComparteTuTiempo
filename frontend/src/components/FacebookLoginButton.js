import React, { useEffect } from "react";
import axios from "axios";

const FacebookLoginButton = ({ onSuccess }) => {
  useEffect(() => {
    // Inicializar el SDK cuando esté cargado
    window.fbAsyncInit = function () {
      window.FB.init({
        appId: "2570329633328551", // 👈 tu APP ID de Facebook
        cookie: true,
        xfbml: true,
        version: "v21.0",
      });
    };
  }, []);

  const handleLogin = () => {
    window.FB.login(
      function (response) {
        if (response.authResponse) {
          window.FB.api("/me", { fields: "name,email,picture" }, function (userInfo) {
            console.log("✅ Datos Facebook:", userInfo);
            onSuccess(userInfo); // 👈 pasamos datos a LoginPage
          });
        } else {
          console.error("❌ Error al autenticar con Facebook:", response);
        }
      },
      { scope: "public_profile,email" }
    );
  };

  return (
    <button
      onClick={handleLogin}
      className="w-full p-2 bg-blue-600 text-white rounded mt-2"
    >
      Iniciar sesión con Facebook
    </button>
  );
};

export default FacebookLoginButton;
