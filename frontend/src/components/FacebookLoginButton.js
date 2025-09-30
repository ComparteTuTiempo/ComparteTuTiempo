import React, { useEffect } from "react";
import { FaFacebookF } from "react-icons/fa";

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
            onSuccess(userInfo);
          });
        } else {
          console.error("❌ Error al autenticar con Facebook:", response);
        }
      },
      { scope: "public_profile,email" }
    );
  };

  return (
    <button onClick={handleLogin} style={styles.button}>
      <FaFacebookF style={styles.icon} />
      Continue with Facebook
    </button>
  );
};

const styles = {
  button: {
    width: "100%",
    marginTop: "10px",
    padding: "12px",
    border: "none",
    borderRadius: "6px",
    backgroundColor: "#1877F2", // azul oficial
    color: "#fff",
    fontSize: "15px",
    fontWeight: "500",
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: "10px",
  },
  icon: { fontSize: "18px" },
};

export default FacebookLoginButton;
