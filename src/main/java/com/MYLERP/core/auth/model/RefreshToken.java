package com.MYLERP.core.auth.model;

import com.MYLERP.core.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "dispositivo", "expiraEn", "revocado"}) // nunca loguear tokenHash
@Entity
@Table(name = "refresh_tokens", schema = "core")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Se guarda el HASH (SHA-256) del refresh token, nunca el valor en texto plano.
    // Igual que con passwords: si la BD se filtra, no se pueden usar los tokens directamente.
    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(length = 150)
    private String dispositivo;

    @Column(name = "expira_en", nullable = false)
    private OffsetDateTime expiraEn;

    @Builder.Default
    @Column(nullable = false)
    private Boolean revocado = false;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
