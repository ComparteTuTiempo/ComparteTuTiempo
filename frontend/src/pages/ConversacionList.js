import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../utils/AuthContext";
import { Link } from "react-router-dom";

export default function ConversationList() {
  const { user, token } = useAuth();
  const [convs, setConvs] = useState([]);

  useEffect(() => {
    if (!user?.correo) return;
    axios.get(`http://localhost:8080/conversaciones/user/${user.correo}`, {
      headers: { Authorization: `Bearer ${token}` }
    }).then(res => setConvs(res.data))
      .catch(err => console.error(err));
      console.log(convs)
  }, [user, token]);

  return (
    <div>
      <h3>Conversaciones</h3>
      <ul>
        {convs.map(c => (
          <li key={c.id}>
            <Link to={`/conversaciones/${c.id}`}>
              {c.titulo || c.participantes.map(p=>p.nombre || p.correo).join(", ")}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
