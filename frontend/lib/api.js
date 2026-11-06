// Cliente HTTP/JSON de la API. Nunca envía el id del usuario: el backend lo obtiene de la sesión.
export class ApiError extends Error {
  constructor(status, mensaje, campos) {
    super(mensaje);
    this.status = status;
    this.campos = campos || {};
  }
}

export async function api(ruta, { method = 'GET', body } = {}) {
  const res = await fetch(`/api${ruta}`, {
    method,
    headers: body !== undefined ? { 'Content-Type': 'application/json' } : {},
    body: body !== undefined ? JSON.stringify(body) : undefined,
    credentials: 'same-origin',
    cache: 'no-store',
  });
  const texto = await res.text();
  let datos = null;
  if (texto) {
    try {
      datos = JSON.parse(texto);
    } catch {
      datos = { mensaje: texto };
    }
  }
  if (!res.ok) {
    const porDefecto = res.status >= 500
      ? 'El servidor no respondió correctamente. Inténtalo de nuevo.'
      : `Error ${res.status}`;
    throw new ApiError(res.status, datos?.mensaje || porDefecto, datos?.campos);
  }
  return datos;
}
