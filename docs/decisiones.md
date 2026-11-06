# Decisiones técnicas

Estado: **Aprobada** (documentada en las guías), **Confirmada** (indicada por un integrante, falta validarla con el equipo o el tutor) o **Pendiente**.

| # | Decisión | Estado | Fuente |
|---|---|---|---|
| D1 | Arquitectura: frontend Next.js + React, API REST con Spring Boot, PostgreSQL. Solo el backend accede a la base. | Aprobada | Guía 13 |
| D2 | Contraseñas solo como hash con sal mediante BCrypt; nunca en texto plano ni en respuestas. | Aprobada | Guía 13 |
| D3 | La API obtiene al usuario de la sesión autenticada; no confía en ids enviados por el cliente. | Aprobada | Guía 13 |
| D4 | **Estrategia de sesión: sesión de servidor** con cookie `JSESSIONID` HttpOnly, SameSite=Lax y 2 h de inactividad. | Confirmada por Roberto Martínez (05/11/2026); validar con el equipo y el tutor | Guía 13 pide acordar una sola estrategia antes de HU-11 |
| D5 | El frontend llama a `/api/...` en su mismo origen y Next.js lo reenvía al backend. Así la cookie funciona sin CORS, y por eso CSRF está deshabilitado. Si el frontend se publica en otro dominio, hay que habilitar CORS y protección CSRF. | Propuesta técnica derivada de D4 | Este repositorio |
| D6 | Mismo mensaje para usuario inexistente y contraseña incorrecta ("Usuario o contraseña incorrectos."). | Propuesta técnica (CP02 pide "mensaje seguro") | Guía 13, CP02 |
| D7 | Roles como catálogo en la tabla `roles` (USUARIO, ENCARGADO); cada usuario tiene un rol. | Aprobada ("entidad Usuario y relación con Rol") | Guía 13, tareas HU-11 |
| D8 | Esquema con migraciones Flyway; las cuentas ficticias se crean al iniciar solo si existe `TECHLOAN_SEED_PASSWORD`. | Propuesta técnica | Guía 13: "datos semilla" sin secretos |

## Pendientes

- Bloqueo de cuenta tras intentos fallidos o límite de intentos: no está documentado; no se implementó.
- Recuperación de contraseña y registro de usuarios: no forman parte de HU-11 en la Guía 13.
- Integración con un directorio institucional: fuera del MVP salvo aprobación expresa (Guía 13).
