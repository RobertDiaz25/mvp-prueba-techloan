'use client';

import Protegida from '@/components/Protegida';
import InicioRol from '@/components/InicioRol';

export default function InicioUsuario() {
  return (
    <Protegida rol="USUARIO">
      <InicioRol endpoint="/usuario/inicio" />
    </Protegida>
  );
}
