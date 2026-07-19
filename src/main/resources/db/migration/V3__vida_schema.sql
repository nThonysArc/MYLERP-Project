-- DOMINIO: VIDA (RECORDATORIOS Y PAGOS)

CREATE TABLE vida.responsabilidades (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES core.usuarios(id) ON DELETE CASCADE,
    nombre VARCHAR(200) NOT NULL,
    tipo VARCHAR(50),
    frecuencia_dias INT NOT NULL CHECK (frecuencia_dias > 0),
    ultima_fecha_cumplimiento TIMESTAMPTZ NULL,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_responsabilidades_updated_at
BEFORE UPDATE ON vida.responsabilidades
FOR EACH ROW EXECUTE FUNCTION core.actualizar_updated_at();

CREATE TABLE vida.registros_cumplimiento (
    id BIGSERIAL PRIMARY KEY,
    responsabilidad_id BIGINT NOT NULL REFERENCES vida.responsabilidades(id) ON DELETE CASCADE,
    fecha_realizada TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION vida.actualizar_ultimo_cumplimiento()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE vida.responsabilidades
    SET ultima_fecha_cumplimiento = NEW.fecha_realizada
    WHERE id = NEW.responsabilidad_id
    AND (ultima_fecha_cumplimiento IS NULL OR ultima_fecha_cumplimiento < NEW.fecha_realizada);
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cumplimiento_actualiza_responsabilidad
AFTER INSERT ON vida.registros_cumplimiento
FOR EACH ROW EXECUTE FUNCTION vida.actualizar_ultimo_cumplimiento();

CREATE TABLE vida.pagos_programados (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES core.usuarios(id) ON DELETE CASCADE,
    nombre VARCHAR(150) NOT NULL,
    monto DECIMAL(12,2) NOT NULL CHECK (monto > 0),
    fecha_vencimiento DATE NOT NULL,
    categoria VARCHAR(100),
    recurrente BOOLEAN DEFAULT FALSE,
    estado VARCHAR(20) DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'PAGADO')),
    deleted_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_pagos_updated_at
BEFORE UPDATE ON vida.pagos_programados
FOR EACH ROW EXECUTE FUNCTION core.actualizar_updated_at();

CREATE VIEW vida.pagos_con_estado_real AS
SELECT
    p.*,
    CASE
        WHEN p.estado = 'PENDIENTE' AND p.fecha_vencimiento < CURRENT_DATE THEN 'VENCIDO'
        ELSE p.estado
    END AS estado_real
FROM vida.pagos_programados p
WHERE p.deleted_at IS NULL;

-- ÍNDICES DE RENDIMIENTO DE VIDA
CREATE INDEX idx_cumplimiento_resp_fecha ON vida.registros_cumplimiento(responsabilidad_id, fecha_realizada);
CREATE INDEX idx_responsabilidades_usuario ON vida.responsabilidades(usuario_id) WHERE activo = TRUE;
CREATE INDEX idx_pagos_usuario_vencimiento ON vida.pagos_programados(usuario_id, fecha_vencimiento) WHERE deleted_at IS NULL;