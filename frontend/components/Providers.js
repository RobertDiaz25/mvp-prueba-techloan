'use client';

import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { api } from '@/lib/api';

const AuthContext = createContext(null);

/** Guarda quién inició sesión, consultando /api/auth/me al cargar la aplicación. */
export default function Providers({ children }) {
  const [usuario, setUsuario] = useState(null);
  const [cargando, setCargando] = useState(true);

  const recargar = useCallback(async () => {
    try {
      setUsuario(await api('/auth/me'));
    } catch {
      setUsuario(null);
    } finally {
      setCargando(false);
    }
  }, []);

  const salir = useCallback(async () => {
    try {
      await api('/auth/logout', { method: 'POST' });
    } finally {
      setUsuario(null);
    }
  }, []);

  useEffect(() => {
    recargar();
  }, [recargar]);

  return (
    <AuthContext.Provider value={{ usuario, cargando, recargar, salir, setUsuario }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
