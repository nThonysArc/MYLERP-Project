package com.MYLERP.finanzas.ingreso.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Builder
public record IngresoDTO(
        Long id,
        Long categoriaId,
        String categoriaNombre,
        BigDecimal monto,
        LocalDate fecha,
        String fuente,
        boolean recurrente,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
