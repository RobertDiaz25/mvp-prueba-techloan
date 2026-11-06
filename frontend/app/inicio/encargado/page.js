'use client';

import Protegida from '@/components/Protegida';
import InicioRol from '@/components/InicioRol';

export default function InicioEncargado() {
  return (
    <Protegida rol="ENCARGADO">
      <InicioRol endpoint="/encargado/inicio" />
    </Protegida>
  );
}
