package com.MYLERP.vida.responsabilidad.model;

import java.time.OffsetDateTime;

import com.MYLERP.shared.model.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@ToString(of = {"id", "nombre", "frecuenciaDias", "ultimaFechaCumplimiento"})
@Entity
@Table(name = "responsabilidades", schema = "vida")
public class Responsabilidad extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 50)
    private String tipo;

    @Column(name = "frecuencia_dias", nullable = false)
    private Integer frecuenciaDias;

    // Desnormalizado: lo mantiene sincronizado un trigger en Postgres
    // (AFTER INSERT en registros_cumplimiento), por eso es de solo lectura aquí.
    @Column(name = "ultima_fecha_cumplimiento", insertable = false, updatable = false)
    private OffsetDateTime ultimaFechaCumplimiento;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;
}
