-- DOMINIO: FINANZAS

CREATE TABLE finanzas.categorias (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NULL REFERENCES core.usuarios(id) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('INGRESO', 'GASTO', 'AMBOS')),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (usuario_id, nombre, tipo)
);

CREATE TABLE finanzas.ingresos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES core.usuarios(id) ON DELETE CASCADE,
    categoria_id BIGINT NOT NULL REFERENCES finanzas.categorias(id),
    monto DECIMAL(12,2) NOT NULL CHECK (monto > 0),
    fecha DATE NOT NULL,
    fuente VARCHAR(150) NOT NULL,
    recurrente BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_ingresos_updated_at
BEFORE UPDATE ON finanzas.ingresos
FOR EACH ROW EXECUTE FUNCTION core.actualizar_updated_at();

CREATE TABLE finanzas.gastos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES core.usuarios(id) ON DELETE CASCADE,
    categoria_id BIGINT NOT NULL REFERENCES finanzas.categorias(id),
    monto DECIMAL(12,2) NOT NULL CHECK (monto > 0),
    fecha DATE NOT NULL,
    descripcion TEXT,
    deleted_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_gastos_updated_at
BEFORE UPDATE ON finanzas.gastos
FOR EACH ROW EXECUTE FUNCTION core.actualizar_updated_at();

CREATE TABLE finanzas.presupuestos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES core.usuarios(id) ON DELETE CASCADE,
    categoria_id BIGINT NULL REFERENCES finanzas.categorias(id),
    periodo VARCHAR(20) NOT NULL CHECK (periodo IN ('SEMANAL', 'MENSUAL')),
    periodo_referencia VARCHAR(50) NOT NULL,
    monto_limite DECIMAL(12,2) NOT NULL CHECK (monto_limite > 0),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (usuario_id, periodo, periodo_referencia, categoria_id)
);

CREATE TRIGGER trg_presupuestos_updated_at
BEFORE UPDATE ON finanzas.presupuestos
FOR EACH ROW EXECUTE FUNCTION core.actualizar_updated_at();

CREATE TABLE finanzas.resumenes_agregados (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES core.usuarios(id) ON DELETE CASCADE,
    periodo_tipo VARCHAR(20) NOT NULL CHECK (periodo_tipo IN ('SEMANAL', 'MENSUAL')),
    periodo_identificador VARCHAR(50) NOT NULL,
    total_gastado DECIMAL(12,2) DEFAULT 0.00,
    total_ingresado DECIMAL(12,2) DEFAULT 0.00,
    ultima_actualizacion TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (usuario_id, periodo_tipo, periodo_identificador)
);

-- ÍNDICES DE RENDIMIENTO DE FINANZAS
CREATE INDEX idx_gastos_usuario_fecha ON finanzas.gastos(usuario_id, fecha) WHERE deleted_at IS NULL;
CREATE INDEX idx_ingresos_usuario_fecha ON finanzas.ingresos(usuario_id, fecha) WHERE deleted_at IS NULL;
CREATE INDEX idx_presupuestos_usuario ON finanzas.presupuestos(usuario_id);

-- CATEGORÍAS PREDEFINIDAS DEL SISTEMA
INSERT INTO finanzas.categorias (usuario_id, nombre, tipo) VALUES
(NULL, 'Alimentación', 'GASTO'),
(NULL, 'Transporte', 'GASTO'),
(NULL, 'Servicios', 'GASTO'),
(NULL, 'Salud', 'GASTO'),
(NULL, 'Entretenimiento', 'GASTO'),
(NULL, 'Salario', 'INGRESO'),
(NULL, 'Freelance', 'INGRESO'),
(NULL, 'Otros', 'AMBOS');