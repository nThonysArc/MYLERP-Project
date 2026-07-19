package com.MYLERP.finanzas.presupuesto.model;

import java.math.BigDecimal;

import com.MYLERP.finanzas.categoria.model.Categoria;
import com.MYLERP.shared.model.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@EqualsAndHashCode(callSuper = false, of = "id")
@ToString(of = {"id", "periodo", "periodoReferencia", "montoLimite"})
@Entity
@Table(
    name = "presupuestos",
    schema = "finanzas",
    // Refleja el UNIQUE de la tabla: evita presupuestos duplicados/solapados
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"usuario_id", "periodo", "periodo_referencia", "categoria_id"}
    )
)
public class Presupuesto extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // NULL = presupuesto general (no restringido a una categoría)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PeriodoTipo periodo;

    // Ancla el presupuesto a un ciclo concreto, ej: '2026-07' o '2026-W28'
    @Column(name = "periodo_referencia", nullable = false, length = 50)
    private String periodoReferencia;

    @Column(name = "monto_limite", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoLimite;
}
