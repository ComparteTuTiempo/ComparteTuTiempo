const AvatarUsuario = ({ src, alt, size = 40 }) => {
  const defaultAvatar = "https://cdn-icons-png.flaticon.com/512/847/847969.png";

  return (
    <img
      src={src && src.trim() !== "" ? src : defaultAvatar}
      alt={alt}
      style={{
        width: `${size}px`,
        height: `${size}px`,
        borderRadius: "50%",
        objectFit: "cover",
        marginRight: "10px",
      }}
    />
  );
};

export default AvatarUsuario;
