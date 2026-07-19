package com.MYLERP.finanzas.gasto.repository;

import com.MYLERP.finanzas.gasto.model.Gasto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    // Listado general paginado (usado cuando no se filtra por fecha).
    Page<Gasto> findByUsuarioIdAndDeletedAtIsNull(Long usuarioId, Pageable pageable);

    // Listado filtrado por rango de fechas (ej: "gastos de julio 2026").
    Page<Gasto> findByUsuarioIdAndFechaBetweenAndDeletedAtIsNull(
            Long usuarioId, LocalDate desde, LocalDate hasta, Pageable pageable
    );

    // Trae un gasto puntual verificando que pertenezca al usuario autenticado
    // (evita que un usuario acceda/edite gastos de otro solo adivinando el id).
    Optional<Gasto> findByIdAndUsuarioIdAndDeletedAtIsNull(Long id, Long usuarioId);

    @Query("""
        SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g
        WHERE g.usuarioId = :usuarioId AND g.fecha BETWEEN :desde AND :hasta AND g.deletedAt IS NULL
        """)
    java.math.BigDecimal sumarPorRango(
            @Param("usuarioId") Long usuarioId, @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta
    );
}
