/** Pantalla de inicio que corresponde a cada rol. */
export function inicioDe(rol) {
  return rol === 'ENCARGADO' ? '/inicio/encargado' : '/inicio/usuario';
}
