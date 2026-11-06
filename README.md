# TechLoan

Sistema de préstamos tecnológicos · Proyecto de Ingeniería de Software.

Estado actual: **MVP inicial (Semana 14) con HU-11 Autenticación**. Usuario y Encargado de Préstamos inician sesión y ven solo las funciones de su rol. Detalle, pruebas y bitácora en [`docs/semana-14/HU-11-autenticacion.md`](docs/semana-14/HU-11-autenticacion.md).

## Tecnología

| Capa | Tecnología |
|---|---|
| Frontend | Next.js 15 + React 19 (`frontend/`) |
| Backend | API REST con Spring Boot 3.3, Java 17, Spring Security (`backend/`) |
| Base de datos | PostgreSQL 16, esquema con Flyway |
| Autenticación | Sesión de servidor (cookie HttpOnly), contraseñas con BCrypt |

Decisiones y su estado de aprobación en [`docs/decisiones.md`](docs/decisiones.md).

## Requisitos

- JDK 17 o superior y Maven 3.9 (o IntelliJ IDEA, que trae Maven)
- Node.js 18.18 o superior
- PostgreSQL 14 o superior, o Docker

## Cómo ejecutarlo

### 1. Variables de entorno

Copia `.env.example` como `.env` en la raíz y cambia las claves. El archivo `.env` no se sube al repositorio.

| Variable | Para qué sirve |
|---|---|
| `DB_PASSWORD` | Contraseña del usuario `techloan` de PostgreSQL |
| `TECHLOAN_SEED_PASSWORD` | Contraseña de las cuentas ficticias de prueba. Si está vacía, no se crean |
| `DB_URL`, `DB_USER`, `PORT` | Opcionales. Por defecto: `jdbc:postgresql://localhost:5432/techloan`, `techloan`, `8080` |

### 2. PostgreSQL

Con Docker, desde la raíz:

```bash
docker compose up -d
```

Sin Docker, crea la base en psql o pgAdmin (usa la misma clave que pusiste en `DB_PASSWORD`):

```sql
CREATE USER techloan WITH PASSWORD 'tu-clave';
CREATE DATABASE techloan OWNER techloan;
```

### 3. Backend (puerto 8080)

El backend lee las variables del entorno. En una terminal con bash:

```bash
cd backend
export DB_PASSWORD=tu-clave
export TECHLOAN_SEED_PASSWORD=tu-clave-demo
mvn spring-boot:run
```

En Windows PowerShell usa `$env:DB_PASSWORD="tu-clave"` y `$env:TECHLOAN_SEED_PASSWORD="tu-clave-demo"`. En IntelliJ, defínelas en Run → Edit Configurations → Environment variables.

Al iniciar, Flyway crea las tablas `roles` y `usuarios` y se crean las cuentas de prueba si la base está vacía.

### 4. Frontend (puerto 3000)

```bash
cd frontend
npm install
npm run dev
```

Abre http://localhost:3000. Si el backend no está en `http://localhost:8080`, copia `frontend/.env.example` como `frontend/.env.local` y cambia `API_URL`.

## Cuentas de prueba

Todas usan la clave que definiste en `TECHLOAN_SEED_PASSWORD`.

| Usuario | Nombre | Rol |
|---|---|---|
| `encargado` | Karla Hernández | Encargado de Préstamos |
| `20220001` | Ana Martínez | Usuario |
| `20220002` | Luis Gómez | Usuario |

## Pruebas

```bash
cd backend
mvn test
```

`AutenticacionHu11Test` cubre CP01–CP04 de la Guía 13 y casos negativos: sin sesión, campos vacíos y cierre de sesión. Usa H2 en memoria con las mismas migraciones, así que no necesita PostgreSQL. Las pruebas manuales P-01 a P-08 están en el documento de la Semana 14. Guarda los resultados en `evidencias/semana-14/`.

## Estructura

```
backend/     API Spring Boot (config, domain, repository, security, service, web)
frontend/    Next.js (app/, components/, lib/)
docs/        decisiones y documentación por semana
evidencias/  capturas, videos y resultados de pruebas reales
```
