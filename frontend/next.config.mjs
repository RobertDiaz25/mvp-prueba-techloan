/**
 * El navegador llama a /api/... en el mismo origen del frontend y Next.js lo reenvía al backend.
 * Así la cookie de sesión (JSESSIONID) funciona sin configurar CORS.
 */
// 127.0.0.1 en lugar de localhost: en Windows, Node puede intentar ::1 (IPv6) y quedarse esperando.
const API_URL = process.env.API_URL || 'http://127.0.0.1:8080';

const nextConfig = {
  async rewrites() {
    return [{ source: '/api/:path*', destination: `${API_URL}/api/:path*` }];
  },
};

export default nextConfig;
