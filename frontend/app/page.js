'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/components/Providers';
import { inicioDe } from '@/lib/rutas';

/** Envía a cada rol a su inicio, o al login si no hay sesión. */
export default function Raiz() {
  const { usuario, cargando } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (cargando) return;
    router.replace(usuario ? inicioDe(usuario.rol) : '/login');
  }, [cargando, usuario, router]);

  return <p className="muted">Cargando…</p>;
}
