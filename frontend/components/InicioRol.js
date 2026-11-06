'use client';

import { useEffect, useState } from 'react';
import { api } from '@/lib/api';

/**
 * Inicio de un rol: consulta al backend las funciones que le corresponden.
 * Si el backend responde 403, el rol no tiene acceso aunque alguien escriba la URL a mano.
 */
export default function InicioRol({ endpoint }) {
  const [datos, setDatos] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api(endpoint).then(setDatos).catch((e) => setError(e.message));
  }, [endpoint]);

  if (error) return <div className="alerta error" role="alert">{error}</div>;
  if (!datos) return <p className="muted">Cargando…</p>;

  return (
    <>
      <div className="encabezado">
        <div>
          <h1>Hola, {datos.nombre.split(' ')[0]}</h1>
          <p className="muted">Ingresaste como <b>{datos.rolNombre}</b>. Estas son las funciones de tu rol.</p>
        </div>
        <span className="rf">HU-11</span>
      </div>
      <ul className="funciones">
        {datos.funciones.map((f) => (
          <li key={f.historia} className={`funcion ${f.disponible ? '' : 'pronto'}`}>
            <div className="funcion-cab">
              <h2>{f.nombre}</h2>
              <span className="codigo pequeno">{f.historia}</span>
            </div>
            <p className="muted">{f.descripcion}</p>
            {!f.disponible && <span className="estado aviso">Disponible en próximas entregas</span>}
          </li>
        ))}
      </ul>
    </>
  );
}
