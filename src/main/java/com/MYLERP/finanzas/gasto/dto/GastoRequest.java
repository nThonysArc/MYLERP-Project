package com.MYLERP.finanzas.gasto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GastoRequest(

        @NotNull
        Long categoriaId,

        @NotNull
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        BigDecimal monto,

        @NotNull
        @PastOrPresent(message = "No se pueden registrar gastos con fecha futura")
        LocalDate fecha,

        String descripcion
) {
}
