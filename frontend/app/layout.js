import './globals.css';
import Providers from '@/components/Providers';
import Nav from '@/components/Nav';

export const metadata = {
  title: 'TechLoan',
  description: 'Sistema de préstamos tecnológicos',
};

export default function RootLayout({ children }) {
  return (
    <html lang="es">
      <body>
        <Providers>
          <Nav />
          <main className="contenido">{children}</main>
        </Providers>
      </body>
    </html>
  );
}
