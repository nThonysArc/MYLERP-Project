package com.MYLERP.finanzas.resumen.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ResumenService {

    // Actualiza (o crea) el agregado SEMANAL y MENSUAL correspondientes a esa fecha.
    void registrarGasto(Long usuarioId, LocalDate fecha, BigDecimal monto);

    void registrarIngreso(Long usuarioId, LocalDate fecha, BigDecimal monto);
}
