package com.MYLERP.vida.pago.model;

import com.MYLERP.shared.model.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = "id")
@ToString(of = {"id", "nombre", "monto", "fechaVencimiento", "estado"})
@Entity
@Table(name = "pagos_programados", schema = "vida")
public class PagoProgramado extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    // 'BANCO', 'SERVICIO', 'CREDITO' — texto libre por ahora, igual que en el script SQL.
    // Si a futuro se vuelve un problema (como con categoria en gastos), se normaliza en su propia tabla.
    @Column(length = 100)
    private String categoria;

    @Builder.Default
    @Column(nullable = false)
    private Boolean recurrente = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
