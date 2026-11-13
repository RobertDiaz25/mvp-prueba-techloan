// Cliente HTTP/JSON de la API. Nunca envía el id del usuario: el backend lo obtiene de la sesión.
export class ApiError extends Error {
  constructor(status, mensaje, campos) {
    super(mensaje);
    this.status = status;
    this.campos = campos || {};
  }
}

// Si el backend no responde en este tiempo, se muestra un error en lugar de quedarse cargando.
const TIEMPO_MAXIMO_MS = 15000;

const SIN_CONEXION = 'No se pudo conectar con el servidor. Revisa que el backend esté en marcha (puerto 8080) e inténtalo de nuevo.';

export async function api(ruta, { method = 'GET', body } = {}) {
  const control = new AbortController();
  const temporizador = setTimeout(() => control.abort(), TIEMPO_MAXIMO_MS);
  let res;
  try {
    res = await fetch(`/api${ruta}`, {
      method,
      headers: body !== undefined ? { 'Content-Type': 'application/json' } : {},
      body: body !== undefined ? JSON.stringify(body) : undefined,
      credentials: 'same-origin',
      cache: 'no-store',
      signal: control.signal,
    });
  } catch (e) {
    // Error de red o tiempo agotado: sin status HTTP.
    throw new ApiError(undefined, e.name === 'AbortError'
      ? 'El servidor tardó demasiado en responder. Revisa que el backend y PostgreSQL estén en marcha.'
      : SIN_CONEXION);
  } finally {
    clearTimeout(temporizador);
  }

  const texto = await res.text();
  let datos = null;
  if (texto) {
    try {
      datos = JSON.parse(texto);
    } catch {
      datos = null;
    }
  }
  if (!res.ok) {
    const porDefecto = res.status >= 500 ? SIN_CONEXION : `Error ${res.status}`;
    throw new ApiError(res.status, datos?.mensaje || porDefecto, datos?.campos);
  }
  return datos;
}
