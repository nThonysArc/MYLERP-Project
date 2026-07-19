package com.MYLERP.finanzas.resumen.service;

import com.MYLERP.finanzas.resumen.repository.ResumenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ResumenServiceImpl implements ResumenService {

    private static final DateTimeFormatter FORMATO_MENSUAL = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final BigDecimal CERO = BigDecimal.ZERO;

    private final ResumenRepository resumenRepository;

    @Override
    @Transactional
    public void registrarGasto(Long usuarioId, LocalDate fecha, BigDecimal monto) {
        actualizarAmbosPeriodos(usuarioId, fecha, monto, CERO);
    }

    @Override
    @Transactional
    public void registrarIngreso(Long usuarioId, LocalDate fecha, BigDecimal monto) {
        actualizarAmbosPeriodos(usuarioId, fecha, CERO, monto);
    }

    private void actualizarAmbosPeriodos(Long usuarioId, LocalDate fecha, BigDecimal montoGasto, BigDecimal montoIngreso) {
        resumenRepository.incrementar(usuarioId, "MENSUAL", identificadorMensual(fecha), montoGasto, montoIngreso);
        resumenRepository.incrementar(usuarioId, "SEMANAL", identificadorSemanal(fecha), montoGasto, montoIngreso);
    }

    private String identificadorMensual(LocalDate fecha) {
        return fecha.format(FORMATO_MENSUAL); // ej: '2026-07'
    }

    private String identificadorSemanal(LocalDate fecha) {
        // ISO-8601: semana empieza en lunes, semana 1 es la que contiene el primer jueves del anio.
        int anioSemana = fecha.get(IsoFields.WEEK_BASED_YEAR);
        int semana = fecha.get(WeekFields.ISO.weekOfWeekBasedYear());
        return String.format(Locale.ROOT, "%d-W%02d", anioSemana, semana); // ej: '2026-W28'
    }
}
