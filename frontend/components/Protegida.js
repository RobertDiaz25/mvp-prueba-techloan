'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/components/Providers';

/**
 * Muestra la página solo si hay sesión y el rol coincide.
 * Es una comodidad de interfaz: la protección real la aplica el backend en cada endpoint.
 */
export default function Protegida({ rol, children }) {
  const { usuario, cargando } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!cargando && !usuario) router.replace('/login');
  }, [cargando, usuario, router]);

  if (cargando || !usuario) return <p className="muted">Cargando…</p>;
  if (rol && usuario.rol !== rol) {
    return (
      <div className="vacio" role="alert">
        <h2>No tienes permiso para ver esta página</h2>
        <p>Esta sección es para el rol {rol === 'ENCARGADO' ? 'Encargado de Préstamos' : 'Usuario'}.</p>
      </div>
    );
  }
  return children;
}
