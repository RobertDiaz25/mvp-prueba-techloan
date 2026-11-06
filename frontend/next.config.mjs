/**
 * El navegador llama a /api/... en el mismo origen del frontend y Next.js lo reenvía al backend.
 * Así la cookie de sesión (JSESSIONID) funciona sin configurar CORS.
 */
const API_URL = process.env.API_URL || 'http://localhost:8080';

const nextConfig = {
  async rewrites() {
    return [{ source: '/api/:path*', destination: `${API_URL}/api/:path*` }];
  },
};

export default nextConfig;
