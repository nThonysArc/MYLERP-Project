package com.MYLERP.finanzas.gasto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Builder;

// Respuesta de la API. Incluye el nombre de la categoria ya resuelto
// para que el frontend no tenga que hacer un segundo request.
@Builder
public record GastoDTO(
        Long id,
        Long categoriaId,
        String categoriaNombre,
        BigDecimal monto,
        LocalDate fecha,
        String descripcion,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
