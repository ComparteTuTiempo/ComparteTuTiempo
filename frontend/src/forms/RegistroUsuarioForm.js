import React from "react";

const UsuarioForm = ({ usuario, onChange, onSubmit, errores, modo = "registro", deshabilitarCorreo = false, mostrarContrasena = true }) => {
  return (
    <div style={styles.container}>
      <h2 style={styles.title}>{modo === "registro" ? "Registro de Usuario" : "Editar Perfil"}</h2>

      <form onSubmit={onSubmit} style={styles.form}>
        {/* Nombre */}
        <input
          name="nombre"
          placeholder="Nombre y Apellidos"
          value={usuario.nombre || ""}
          onChange={onChange}
          style={styles.input}
          required
        />
        {errores.nombre && <span style={styles.error}>{errores.nombre}</span>}

        {/* Correo */}
        <input
          name="correo"
          placeholder="Correo electrónico"
          type="email"
          value={usuario.correo || ""}
          onChange={onChange}
          style={styles.input}
          required
          disabled={deshabilitarCorreo}
        />
        {errores.correo && <span style={styles.error}>{errores.correo}</span>}

        {/* Contraseña */}
        {mostrarContrasena && (
          <>
            <input
              name="contrasena"
              placeholder="Contraseña"
              type="password"
              value={usuario.contrasena || ""}
              onChange={onChange}
              style={styles.input}
              required={modo === "registro"}
            />
            {errores.contrasena && <span style={styles.error}>{errores.contrasena}</span>}
          </>
        )}

        {/* Fecha de nacimiento */}
        <input
          name="fechaNacimiento"
          placeholder="Fecha de Nacimiento"
          type="date"
          value={usuario.fechaNacimiento || ""}
          onChange={onChange}
          style={styles.input}
        />
        {errores.fechaNacimiento && <span style={styles.error}>{errores.fechaNacimiento}</span>}

        {/* Biografía */}
        <textarea
          name="biografia"
          placeholder="Biografía"
          value={usuario.biografia || ""}
          onChange={onChange}
          style={{ ...styles.input, height: "80px" }}
        />
        {errores.biografia && <span style={styles.error}>{errores.biografia}</span>}

        {/* Foto de perfil */}
        <input
          name="fotoPerfil"
          type="file"
          accept="image/*"
          onChange={onChange}
          style={styles.input}
        />
        <input
          name="fotoPerfil"
          placeholder="O pega una URL de la foto"
          value={usuario.fotoPerfil?.startsWith("data:") ? "" : usuario.fotoPerfil || ""}
          onChange={onChange}
          style={styles.input}
        />
        {errores.fotoPerfil && <span style={styles.error}>{errores.fotoPerfil}</span>}

        {/* Ubicación */}
        <input
          name="ubicacion"
          placeholder="Ubicación"
          value={usuario.ubicacion || ""}
          onChange={onChange}
          style={styles.input}
        />
        {errores.ubicacion && <span style={styles.error}>{errores.ubicacion}</span>}

        {/* Método de autenticación */}
        <select
          name="metodoAutenticacion"
          value={usuario.metodoAutenticacion || "correo"}
          onChange={onChange}
          style={styles.input}
        >
          <option value="correo">Correo</option>
          <option value="google">Google</option>
          <option value="facebook">Facebook</option>
        </select>
        {errores.metodoAutenticacion && <span style={styles.error}>{errores.metodoAutenticacion}</span>}

        <button type="submit" style={styles.button}>
          {modo === "registro" ? "Registrarse" : "Guardar cambios"}
        </button>
      </form>
    </div>
  );
};

const styles = {
  container: {
    backgroundColor: "#000",
    padding: "30px",
    borderRadius: "12px",
    boxShadow: "0 0 15px #ff6f00",
    width: "100%",
    maxWidth: "400px",
    color: "#fff",
  },
  title: {
    textAlign: "center",
    color: "#ff6f00",
    marginBottom: "20px",
  },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "15px",
  },
  input: {
    padding: "10px",
    borderRadius: "6px",
    border: "1px solid #ff6f00",
    backgroundColor: "#fff",
    color: "#000",
    fontSize: "14px",
  },
  button: {
    padding: "12px",
    borderRadius: "6px",
    border: "none",
    backgroundColor: "#ff6f00",
    color: "#fff",
    fontWeight: "bold",
    cursor: "pointer",
    transition: "0.3s",
  },
  error: {
    color: "#ff6f00",
    fontSize: "12px",
    marginTop: "-10px",
    marginBottom: "10px",
  },
};

export default UsuarioForm;
