package com.MYLERP.vida.pago.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Mapea la VISTA vida.pagos_con_estado_real (no una tabla).
 * @Immutable le dice a Hibernate que nunca intente hacer INSERT/UPDATE/DELETE
 * sobre esta entidad — es solo para lectura de reportes/listados con el
 * estado_real ('VENCIDO' calculado) ya resuelto por Postgres.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "nombre", "estado", "estadoReal"})
@Entity
@Immutable
@Table(name = "pagos_con_estado_real", schema = "vida")
public class PagoConEstadoReal {

    @Id
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    private String nombre;

    private BigDecimal monto;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    private String categoria;

    private Boolean recurrente;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    // Columna calculada por la vista: 'PENDIENTE' | 'PAGADO' | 'VENCIDO'
    @Column(name = "estado_real")
    private String estadoReal;
}
