package com.MYLERP.shared.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Campos de auditoría comunes a la mayoría de tablas.
 * created_at / updated_at son manejados por Postgres (DEFAULT + triggers),
 * por eso se marcan como insertable = false, updatable = false:
 * Hibernate solo los LEE, nunca los escribe.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class Auditable {

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
