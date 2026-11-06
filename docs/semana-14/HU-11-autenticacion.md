# Semana 14 · MVP inicial · HU-11 Autenticación

Proyecto TechLoan · Ingeniería de Software · Fase 3 (MVP y entrega final)

## 1. Historia seleccionada

| Elemento | Registro |
|---|---|
| Código y nombre | **HU-11 Autenticación** (RF-11) |
| Usuario y necesidad | Como Usuario, quiero iniciar sesión con mis credenciales institucionales para acceder a las funciones permitidas según mi rol. Aplica a los dos actores del MVP: Usuario solicitante y Encargado de Préstamos. |
| Criterio de aceptación | Dado que existe un usuario registrado, cuando presenta credenciales válidas, entonces la API lo autentica y le permite acceder únicamente a las funciones de su rol. |
| Datos requeridos | Usuario (carné o usuario institucional), contraseña, rol. |
| Reglas y validaciones | Usuario y contraseña obligatorios. Contraseñas solo como hash BCrypt. Ninguna respuesta devuelve contraseña ni hash. La API obtiene la identidad de la sesión. Cada endpoint protegido valida la sesión y el rol en el backend. |
| Pantalla asociada | Inicio de sesión (prototipo P01 de la Semana 10) e inicio de cada rol. |
| Dependencias o dudas | La estrategia de sesión debía acordarse antes de implementar (Guía 13). Se confirmó **sesión de servidor** (ver `docs/decisiones.md`, D4); falta validarla con el equipo y el tutor. |

### Justificación

- Es la historia que el cronograma de la Guía 13 asigna a la Semana 14.
- Tiene criterio de aceptación y casos de prueba definidos (CP01–CP04).
- Las otras cuatro historias del MVP (HU-03, HU-12, HU-13, HU-17) requieren un usuario autenticado, así que HU-11 es la de menor dependencia y la base de las demás.
- Se puede recorrer de inicio a fin y probar sola, como pide la Guía 14.

### Contradicción encontrada en los documentos

En "Requisitos funcionales mejorados" la autenticación figura como **RF-13**. En la Guía 13 (más reciente y corregida) figura como **HU-11 (RF-11)**. Se aplicó la numeración de la Guía 13. Además, la lista completa HU-01 a HU-19 no estaba disponible al momento de este trabajo; conviene agregarla a `docs/` para confirmar la trazabilidad.

### Alcance exacto

Incluido:

- Inicio de sesión con usuario y contraseña.
- Sesión de servidor con cookie HttpOnly.
- Consulta de la identidad actual (`/api/auth/me`).
- Inicio por rol, con las funciones que le corresponden según el MVP.
- Bloqueo por rol en el backend (401 sin sesión, 403 con otro rol).
- Cierre de sesión.
- Cuentas ficticias de prueba.

Excluido expresamente:

- Registro de usuarios y recuperación de contraseña.
- Bloqueo por intentos fallidos.
- Integración con un directorio institucional.
- Cualquier función de HU-03, HU-12, HU-13 y HU-17. El inicio de cada rol las muestra como "Disponible en próximas entregas"; no hay pantallas ni endpoints de esas historias.

## 2. Tareas técnicas

| # | Capa | Tarea | Resultado verificable | Estado |
|---|---|---|---|---|
| T1 | Configuración | Estructura `backend/` (Spring Boot 3.3, Java 17) y `frontend/` (Next.js 15) | Proyectos con `pom.xml` y `package.json` | Hecho |
| T2 | Datos | Migración `V1__roles_y_usuarios.sql`: tablas `roles` y `usuarios`, relación usuario → rol | Flyway crea las tablas al iniciar | Hecho, sin ejecutar |
| T3 | Datos | Cuentas ficticias con BCrypt, contraseña desde `TECHLOAN_SEED_PASSWORD` | 3 usuarios creados si la base está vacía | Hecho, sin ejecutar |
| T4 | Lógica | Autenticación con Spring Security, sesión de servidor y cambio de id de sesión al entrar | `POST /api/auth/login` crea la sesión | Hecho, sin ejecutar |
| T5 | Lógica | Permisos por rol y respuestas 401 y 403 en JSON | Usuario no entra a `/api/encargado/**` | Hecho, sin ejecutar |
| T6 | Lógica | Validación de campos y manejo de errores con formato único | 400 indica qué campo falta | Hecho, sin ejecutar |
| T7 | Interfaz | Pantalla de login con campos obligatorios, mensajes de error y estado de carga | Formulario funcional | Hecho, sin ejecutar |
| T8 | Interfaz | Inicio por rol, protección de rutas y cierre de sesión | Cada rol ve su inicio | Hecho, sin ejecutar |
| T9 | Pruebas | `AutenticacionHu11Test` con 9 pruebas automáticas | `mvn test` | Escrito, **sin ejecutar** |
| T10 | Documentación | Este documento, decisiones, README y carpeta de evidencias | Documentos en el repositorio | Hecho |

## 3. Arquitectura y componentes

```
Navegador ── HTTP/JSON (/api) ──> Next.js (proxy) ──> API Spring Boot ── JDBC ──> PostgreSQL
```

| Capa | Componente | Responsabilidad |
|---|---|---|
| Frontend | `app/login/page.js` | Formulario, validación de campos, mensajes, redirección según el rol |
| Frontend | `app/inicio/usuario`, `app/inicio/encargado` | Inicio de cada rol |
| Frontend | `components/Providers.js` | Estado de la sesión (consulta `/api/auth/me`) |
| Frontend | `components/Protegida.js` | Redirige al login sin sesión y avisa si el rol no corresponde (solo comodidad; la protección real está en el backend) |
| Frontend | `next.config.mjs` | Reenvía `/api/*` al backend en el mismo origen |
| Backend | `config/SecurityConfig.java` | Reglas de acceso por rol, BCrypt, respuestas 401 y 403 |
| Backend | `web/AuthController.java` | Login, logout, identidad actual |
| Backend | `web/InicioController.java` + `service/InicioService.java` | Inicio de cada rol con sus funciones |
| Backend | `security/UsuarioDetailsService.java`, `UsuarioPrincipal.java` | Carga del usuario y su rol; borra el hash de la sesión |
| Backend | `config/UsuariosSemilla.java` | Cuentas ficticias desde variable de entorno |
| Backend | `web/ApiExceptionHandler.java` | Errores con formato `{mensaje, campos}` |

## 4. Endpoints

| Método | Ruta | Acceso | Cuerpo | Respuestas |
|---|---|---|---|---|
| POST | `/api/auth/login` | Público | `{"username":"20220001","password":"..."}` | 200 `{id, username, nombre, rol, rolNombre}` · 400 campos vacíos · 401 credenciales incorrectas |
| POST | `/api/auth/logout` | Público | — | 204 |
| GET | `/api/auth/me` | Con sesión | — | 200 usuario · 401 |
| GET | `/api/usuario/inicio` | Rol USUARIO | — | 200 `{nombre, rol, rolNombre, funciones[]}` · 401 · 403 |
| GET | `/api/encargado/inicio` | Rol ENCARGADO | — | 200 · 401 · 403 |

## 5. Modelo de datos

```
roles (id, codigo UNIQUE, nombre)          -- USUARIO, ENCARGADO
usuarios (id, username UNIQUE, nombre, password_hash, rol_id → roles.id)
```

- `password_hash` guarda el hash BCrypt, que incluye la sal.
- Los dos roles se insertan en la migración. Los usuarios de prueba los crea `UsuariosSemilla` al iniciar.

## 6. Pruebas

Las pruebas se escribieron, pero **todavía no se han ejecutado**. El entorno donde se desarrolló no tenía acceso a Maven Central ni a npm para descargar dependencias. Por eso la columna "Resultado obtenido" está vacía: se completa al ejecutarlas, guardando la evidencia en `evidencias/semana-14/`.

### Automáticas (`backend/src/test/java/com/techloan/AutenticacionHu11Test.java`)

Ejecutar desde la raíz con `.\scripts\ejecutar-pruebas.ps1` (Windows) o `bash scripts/ejecutar-pruebas.sh`. El script corre `mvn test` y guarda la salida con fecha en `evidencias/semana-14/`.

| Caso | Escenario | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|
| CP01 | Login como Usuario y acceso a su inicio | 200, rol USUARIO, funciones del Usuario | | No ejecutada |
| CP01b | Login como Encargado y acceso a su inicio | 200, rol ENCARGADO, funciones del Encargado | | No ejecutada |
| CP02 | Contraseña incorrecta | 401 "Usuario o contraseña incorrectos.", sin sesión creada | | No ejecutada |
| CP02b | Usuario inexistente | 401 con el mismo mensaje que CP02 | | No ejecutada |
| CP03 | Usuario pide `/api/encargado/inicio`; Encargado pide `/api/usuario/inicio` | 403 en ambos | | No ejecutada |
| CP04 | Revisar base y respuesta del login | Solo hashes `$2…`; la respuesta no contiene contraseña ni hash | | No ejecutada |
| CP05 | Endpoints protegidos sin sesión | 401 | | No ejecutada |
| CP06 | Usuario y contraseña vacíos | 400 con `campos.username` y `campos.password` | | No ejecutada |
| CP07 | Cerrar sesión | 204; la sesión queda invalidada | | No ejecutada |

### Manuales (Guía 14, Actividad 3)

| ID | Escenario | Datos de entrada | Pasos | Resultado esperado | Resultado obtenido |
|---|---|---|---|---|---|
| P-01 | Flujo exitoso (Usuario) | `20220001` y la clave de `TECHLOAN_SEED_PASSWORD` | Abrir http://localhost:3000, ingresar, pulsar Ingresar | Entra a "Hola, Ana" con las 3 funciones del Usuario | |
| P-02 | Dato faltante | Usuario vacío, contraseña vacía | Pulsar Ingresar | Mensajes bajo cada campo; no se envía la solicitud | |
| P-03 | Dato inválido | `20220001` y una clave incorrecta | Pulsar Ingresar | "Usuario o contraseña incorrectos."; el campo de contraseña se vacía | |
| P-04 | Flujo exitoso (Encargado) | `encargado` y la clave de prueba | Ingresar | Entra al inicio del Encargado con sus 2 funciones | |
| P-05 | Acceso por URL a otro rol | Sesión de `20220001` | Escribir `/inicio/encargado` en la barra | "No tienes permiso para ver esta página" | |
| P-06 | Persistencia y hash en PostgreSQL | — | En psql o pgAdmin: `SELECT username, password_hash FROM usuarios;` | Tres filas; cada hash empieza con `$2` | |
| P-07 | Error de comunicación | Backend apagado | Intentar ingresar | "No se pudo conectar con el servidor…" | |
| P-08 | Cerrar sesión | Sesión activa | Pulsar Cerrar sesión y luego ir a `/inicio/usuario` | Vuelve al login | |

## 7. Bitácora técnica · Semana 14

| Campo | Registro |
|---|---|
| Historia implementada | HU-11 Autenticación (RF-11) |
| Objetivo del incremento | Que Usuario y Encargado inicien sesión y vean solo las funciones de su rol. |
| Tareas realizadas | T1–T10 de la sección 2: configuración, migración, semilla, seguridad, endpoints, interfaz, pruebas y documentación. |
| Decisiones técnicas | Sesión de servidor (D4) por ser la estrategia confirmada y más simple para el MVP. Proxy de Next.js para trabajar en un solo origen (D5). Mensaje único ante credenciales incorrectas (D6). Contraseña de las cuentas de prueba solo por variable de entorno (D8). |
| Bloqueos o defectos | 1) Al construir no había acceso a Maven Central ni a npm, así que el código no se pudo compilar ni ejecutar. Se revisó la sintaxis Java con `javac` y la de JSX con un analizador de Babel, sin errores. 2) Se intentó ejecutar las pruebas en el equipo de Roberto mediante el entorno Linux de Claude, pero no inició por falta de espacio en disco (requiere hasta 17 GB). Se agregó `scripts/ejecutar-pruebas.ps1` para correr `mvn test` en Windows y guardar la salida en `evidencias/semana-14/`. **Pendiente: ejecutar las pruebas automáticas y manuales.** 3) Numeración distinta de RF entre documentos (sección 1); se aplicó la Guía 13. |
| Pruebas ejecutadas | Ninguna todavía (ver sección 6). |
| Evidencia | Este documento y el Pull Request de la rama `hu-11-autenticacion`. Capturas y salida de pruebas: pendientes en `evidencias/semana-14/`. |
| Uso de IA | Claude (asistente de IA) generó el código y la documentación a partir de las Guías 9, 10, 13 y 14 y de los requisitos mejorados. Validación pendiente por el equipo: ejecutar las pruebas y revisar el Pull Request. |
| Próximo paso (Semana 15) | Ejecutar y registrar las pruebas de HU-11, corregir defectos y empezar HU-03 Consulta de equipos sobre esta autenticación. |

## 8. Limitaciones conocidas

- Código no compilado ni ejecutado todavía (ver bitácora).
- Sin bloqueo por intentos fallidos ni recuperación de contraseña (no documentados para HU-11).
- CSRF deshabilitado porque la API se consume en el mismo origen con cookie SameSite=Lax; si se cambia el despliegue, revisar D5.
- La sesión vive en la memoria del servidor: reiniciar el backend cierra todas las sesiones.
- No hay pruebas automáticas del frontend; se cubre con las pruebas manuales P-01 a P-08.
