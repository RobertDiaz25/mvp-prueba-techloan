-- HU-11 Autenticación · roles y usuarios

-- Catálogo de roles del MVP
CREATE TABLE roles (
    id     BIGSERIAL   PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(60) NOT NULL
);

INSERT INTO roles (codigo, nombre) VALUES
    ('USUARIO', 'Usuario'),
    ('ENCARGADO', 'Encargado de Préstamos');

CREATE TABLE usuarios (
    id            BIGSERIAL    PRIMARY KEY,
    username      VARCHAR(40)  NOT NULL UNIQUE,   -- carné o usuario institucional
    nombre        VARCHAR(120) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,          -- solo hash BCrypt (incluye la sal); nunca texto plano
    rol_id        BIGINT       NOT NULL REFERENCES roles (id)
);

CREATE INDEX ix_usuarios_rol ON usuarios (rol_id);
