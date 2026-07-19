package com.MYLERP.vida.responsabilidad.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "fechaRealizada"})
@Entity
@Table(name = "registros_cumplimiento", schema = "vida")
public class RegistroCumplimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsabilidad_id", nullable = false)
    private Responsabilidad responsabilidad;

    // Al insertar un registro nuevo, el trigger de Postgres actualiza
    // automáticamente responsabilidades.ultima_fecha_cumplimiento
    @Builder.Default
    @Column(name = "fecha_realizada", nullable = false)
    private OffsetDateTime fechaRealizada = OffsetDateTime.now();

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
