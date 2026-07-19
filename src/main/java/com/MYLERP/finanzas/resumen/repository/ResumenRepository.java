package com.MYLERP.finanzas.resumen.repository;

import com.MYLERP.finanzas.resumen.model.ResumenAgregado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

public interface ResumenRepository extends JpaRepository<ResumenAgregado, Long> {

    Optional<ResumenAgregado> findByUsuarioIdAndPeriodoTipoAndPeriodoIdentificador(
            Long usuarioId, String periodoTipo, String periodoIdentificador
    );

    /**
     * UPSERT atomico a nivel de fila (INSERT ... ON CONFLICT DO UPDATE).
     * Es intencional que esto sea SQL nativo y no "leer, sumar en Java, guardar":
     * con varios usuarios registrando gastos en paralelo (o varias instancias del
     * backend corriendo), un read-then-write normal puede perder incrementos por
     * condiciones de carrera. El UPSERT delega la atomicidad a Postgres.
     */
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO finanzas.resumenes_agregados
            (usuario_id, periodo_tipo, periodo_identificador, total_gastado, total_ingresado, ultima_actualizacion)
        VALUES (:usuarioId, :periodoTipo, :periodoIdentificador, :montoGasto, :montoIngreso, now())
        ON CONFLICT (usuario_id, periodo_tipo, periodo_identificador)
        DO UPDATE SET
            total_gastado = finanzas.resumenes_agregados.total_gastado + EXCLUDED.total_gastado,
            total_ingresado = finanzas.resumenes_agregados.total_ingresado + EXCLUDED.total_ingresado,
            ultima_actualizacion = now()
        """, nativeQuery = true)
    void incrementar(
            @Param("usuarioId") Long usuarioId,
            @Param("periodoTipo") String periodoTipo,
            @Param("periodoIdentificador") String periodoIdentificador,
            @Param("montoGasto") BigDecimal montoGasto,
            @Param("montoIngreso") BigDecimal montoIngreso
    );
}
