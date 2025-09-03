import React from "react";
import { ShieldCheck } from "lucide-react";
import { motion } from "framer-motion";
import { Link } from "react-router-dom";

const PoliticaPrivacidad = () => {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center p-6">
      {/* Header */}
      <div
        initial={{ opacity: 0, y: -15 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex flex-col items-center mb-8 text-center"
      >
        <h1 className="text-3xl font-bold">Política de Privacidad</h1>
        <p className="text-gray-600 mt-2 max-w-xl">
          Tu privacidad es importante. Aquí te explicamos cómo tratamos tus
          datos personales en nuestro Banco de Tiempo.
        </p>
      </div>

      
      <div className="bg-white shadow-lg rounded-2xl max-w-3xl w-full p-6">
        <div className="h-[60vh] overflow-y-auto pr-4 space-y-6 text-gray-700 leading-relaxed">
          <section>
            <h2 className="text-xl font-semibold mb-2">
              1. Responsable del tratamiento
            </h2>
            <p>
              [Nombre de la entidad o persona responsable] <br />
              [Dirección de contacto] <br />
              [Correo electrónico de contacto]
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold mb-2">
              2. Finalidad del tratamiento
            </h2>
            <p>
              En el Banco de Tiempo recopilamos y tratamos los datos personales de los usuarios con las siguientes finalidades:

              <li>Gestionar el registro de usuarios en la plataforma.</li>

              <li>Facilitar el intercambio de horas de servicio entre usuarios.</li>

              <li>Mantener la seguridad y el correcto funcionamiento del sistema.</li>

              <li>Enviar comunicaciones relacionadas con el funcionamiento del Banco de Tiempo.</li>

              <li>En ningún caso se utilizarán los datos para finalidades comerciales ajenas al servicio.</li>
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold mb-2">
              3. Datos que recopilamos
            </h2>
            <ul className="list-disc ml-6">
              <li>Datos de identificación: nombre, apellidos, nombre de usuario.</li>
              <li>Datos de contacto: correo electrónico, número de teléfono (opcional según configuración).</li>
              <li>Datos de perfil: habilidades, intereses y disponibilidad de tiempo.</li>
              <li>Historial de intercambios dentro del Banco de Tiempo.</li>
            </ul>
            No solicitamos ni tratamos categorías especiales de datos personales (como salud, religión o ideología).
          </section>

          <section>
            <h2 className="text-xl font-semibold mb-2">4. Tus derechos</h2>
            El tratamiento de los datos se realiza conforme a:
            <p>
              <li>Acceder a sus datos personales.</li>

              <li>Rectificar los datos inexactos o incompletos.</li>

              <li>Solicitar la supresión (derecho al olvido) de sus datos cuando ya no sean necesarios o el usuario retire su consentimiento.</li>

              <li>Solicitar la limitación del tratamiento de sus datos.</li>

              <li>Oponerse al tratamiento de sus datos en determinadas circunstancias.</li>
            </p>
            Para ejercer estos derechos, el usuario puede escribir a: [correo electrónico de contacto del responsable].
          </section>

          <section>
            <h2 className="text-xl font-semibold mb-2">
              5. Conservación y seguridad
            </h2>
            <p>
              Los datos se conservarán mientras la cuenta del usuario permanezca activa. Una vez solicitada la baja, 
              los datos serán eliminados en un plazo máximo de 30 días, salvo aquellos que deban mantenerse bloqueados 
              por obligaciones legales.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold mb-2">
              6. Cambios en esta política
            </h2>
            <p>
              Podemos actualizar esta política para reflejar cambios en la
              normativa o en nuestros procesos. La versión vigente estará siempre
              publicada aquí.
            </p>
          </section>
          <section>
            <h2 className="text-xl font-semibold mb-2">
              7. Cesión y transferencia de datos
            </h2>
            <p>
              Los datos no se cederán a terceros salvo obligación legal.
              Si el servicio utiliza proveedores externos (por ejemplo, hosting, correo electrónico), 
              estos actúan como encargados del tratamiento, con los contratos correspondientes que 
              garantizan la seguridad de la información.
            </p>
          </section>
          <section>
            <h2 className="text-xl font-semibold mb-2">
              8. Seguridad de los datos
            </h2>
            <p>
              Implementamos medidas técnicas y organizativas adecuadas para garantizar la confidencialidad, 
              integridad y disponibilidad de los datos personales, protegiéndolos frente a accesos no 
              autorizados, pérdida o destrucción accidental.
            </p>
          </section>
          <section>
            <h2 className="text-xl font-semibold mb-2">
              9. Modificaciones de la política de privacidad
            </h2>
            <p>
              Podremos actualizar esta política de privacidad en cualquier momento. La versión vigente 
              estará siempre publicada en nuestra página web y se avisará por correo de su actualización.
            </p>
          </section>
        </div>
      </div>
    </div>
  );
};

export default PoliticaPrivacidad;
