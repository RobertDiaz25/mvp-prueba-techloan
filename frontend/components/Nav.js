'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/components/Providers';
import { inicioDe } from '@/lib/rutas';

/** Barra superior: identidad y rol del usuario autenticado, y cierre de sesión. */
export default function Nav() {
  const { usuario, salir } = useAuth();
  const router = useRouter();
  if (!usuario) return null;

  async function cerrarSesion() {
    await salir();
    router.replace('/login');
  }

  return (
    <header className="barra">
      <div className="barra-in">
        <Link href={inicioDe(usuario.rol)} className="marca">
          <span className="logo" aria-hidden="true">TL</span> TechLoan
        </Link>
        <span className="espacio" />
        <div className="sesion">
          <span>{usuario.nombre} · {usuario.rolNombre}</span>
          <button type="button" className="btn sm" onClick={cerrarSesion}>Cerrar sesión</button>
        </div>
      </div>
    </header>
  );
}
