'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { api } from '@/lib/api';
import { useAuth } from '@/components/Providers';
import { inicioDe } from '@/lib/rutas';

/** HU-11: inicio de sesión con credenciales institucionales. */
export default function Login() {
  const { usuario, cargando, setUsuario } = useAuth();
  const router = useRouter();
  const [datos, setDatos] = useState({ username: '', password: '' });
  const [errores, setErrores] = useState({});
  const [mensaje, setMensaje] = useState('');
  const [enviando, setEnviando] = useState(false);

  // Si ya hay sesión, no tiene sentido mostrar el login.
  useEffect(() => {
    if (!cargando && usuario) router.replace(inicioDe(usuario.rol));
  }, [cargando, usuario, router]);

  function cambiar(campo) {
    return (e) => setDatos({ ...datos, [campo]: e.target.value });
  }

  async function enviar(e) {
    e.preventDefault();
    const faltan = {};
    if (!datos.username.trim()) faltan.username = 'Escribe tu usuario o carné.';
    if (!datos.password) faltan.password = 'Escribe tu contraseña.';
    setErrores(faltan);
    setMensaje('');
    if (Object.keys(faltan).length) return;

    setEnviando(true);
    try {
      const u = await api('/auth/login', {
        method: 'POST',
        body: { username: datos.username.trim(), password: datos.password },
      });
      setUsuario(u);
      router.replace(inicioDe(u.rol));
    } catch (err) {
      setErrores(err.campos || {});
      setMensaje(err.status === undefined || err.status >= 500
        ? 'No se pudo conectar con el servidor. Revisa que el backend esté en marcha e inténtalo de nuevo.'
        : err.message);
      setDatos((d) => ({ ...d, password: '' }));
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="login">
      <form className="tarjeta login-tarjeta" onSubmit={enviar} noValidate>
        <div className="marca"><span className="logo" aria-hidden="true">TL</span> TechLoan</div>
        <div>
          <h1>Iniciar sesión</h1>
          <p className="muted">Ingresa con tu carné o usuario institucional.</p>
        </div>
        {mensaje && <div className="alerta error" role="alert">{mensaje}</div>}
        <p className="pequeno muted">Los campos con <span className="req">*</span> son obligatorios.</p>
        <label className={`campo ${errores.username ? 'invalido' : ''}`}>
          <span>Usuario o carné <span className="req">*</span></span>
          <input id="username" value={datos.username} onChange={cambiar('username')} autoComplete="username"
                 placeholder="Ej. 20220001" maxLength={40} aria-invalid={!!errores.username} />
          {errores.username && <span className="error-campo">{errores.username}</span>}
        </label>
        <label className={`campo ${errores.password ? 'invalido' : ''}`}>
          <span>Contraseña <span className="req">*</span></span>
          <input id="password" type="password" value={datos.password} onChange={cambiar('password')}
                 autoComplete="current-password" maxLength={72} aria-invalid={!!errores.password} />
          {errores.password && <span className="error-campo">{errores.password}</span>}
        </label>
        <button className="btn primario" disabled={enviando}>{enviando ? 'Ingresando…' : 'Ingresar'}</button>
      </form>
    </div>
  );
}
