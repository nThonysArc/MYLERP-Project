package com.MYLERP.finanzas.ingreso.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IngresoRequest(

        @NotNull
        Long categoriaId,

        @NotNull
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        BigDecimal monto,

        @NotNull
        @PastOrPresent(message = "No se pueden registrar ingresos con fecha futura")
        LocalDate fecha,

        @NotBlank
        String fuente,

        boolean recurrente
) {
}
