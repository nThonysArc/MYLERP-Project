package com.MYLERP.finanzas.resumen.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.MYLERP.finanzas.presupuesto.model.PeriodoTipo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "periodoTipo", "periodoIdentificador", "totalGastado", "totalIngresado"})
@Entity
@Table(
    name = "resumenes_agregados",
    schema = "finanzas",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"usuario_id", "periodo_tipo", "periodo_identificador"}
    )
)
public class ResumenAgregado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_tipo", nullable = false, length = 20)
    private PeriodoTipo periodoTipo;

    @Column(name = "periodo_identificador", nullable = false, length = 50)
    private String periodoIdentificador;

    @Builder.Default
    @Column(name = "total_gastado", precision = 12, scale = 2)
    private BigDecimal totalGastado = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_ingresado", precision = 12, scale = 2)
    private BigDecimal totalIngresado = BigDecimal.ZERO;

    // Este campo SÍ se actualiza desde el service (no hay trigger de Postgres para él),
    // cada vez que se recalcula el agregado tras un nuevo gasto/ingreso.
    @Column(name = "ultima_actualizacion")
    private OffsetDateTime ultimaActualizacion;
}
