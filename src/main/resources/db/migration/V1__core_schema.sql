-- SCHEMAS POR DOMINIO
CREATE SCHEMA IF NOT EXISTS core;
CREATE SCHEMA IF NOT EXISTS finanzas;
CREATE SCHEMA IF NOT EXISTS vida;

-- FUNCIÓN GENÉRICA PARA updated_at (se reutiliza en todas las tablas)
CREATE OR REPLACE FUNCTION core.actualizar_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- DOMINIO: CORE (USUARIOS Y AUTENTICACIÓN)
CREATE TABLE core.usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(100),
    zona_horaria VARCHAR(50) DEFAULT 'America/Lima',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_usuarios_updated_at
BEFORE UPDATE ON core.usuarios
FOR EACH ROW EXECUTE FUNCTION core.actualizar_updated_at();

-- Refresh tokens: uno por sesion/dispositivo, revocable individualmente.
CREATE TABLE core.refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES core.usuarios(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE, -- se guarda el HASH del token, nunca el token en texto plano
    dispositivo VARCHAR(150), -- Tipo de dspositivo o navegador, para identificar la sesión
    expira_en TIMESTAMPTZ NOT NULL,
    revocado BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_tokens_usuario ON core.refresh_tokens(usuario_id) WHERE revocado = FALSE;
CREATE INDEX idx_refresh_tokens_hash ON core.refresh_tokens(token_hash) WHERE revocado = FALSE;