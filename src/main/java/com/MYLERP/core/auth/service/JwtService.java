package com.MYLERP.core.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;

/**
 * Encargado exclusivamente de:
 *  - Generar y validar el ACCESS token (JWT firmado, corta duracion, sin estado en BD).
 *  - Generar el valor en texto plano de un REFRESH token opaco (no es JWT, es un
 *    string aleatorio) y calcular su hash SHA-256 para guardarlo en BD.
 *
 * El AuthService es quien decide QUE hacer con estos tokens (guardarlos, revocarlos, etc).
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTokenExpirationMinutes;
    private final SecureRandom secureRandom = new SecureRandom();

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-minutes}") long accessTokenExpirationMinutes
    ) {
        // El secreto debe tener al menos 256 bits (32 bytes) para HS256.
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMinutes * 60;
    }

    public String generarAccessToken(Long usuarioId, String email) {
        Instant ahora = Instant.now();
        Instant expiracion = ahora.plusSeconds(accessTokenExpirationMinutes * 60);

        return Jwts.builder()
                .subject(String.valueOf(usuarioId))
                .claim("email", email)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expiracion))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Lanza JwtException (o subclases: ExpiredJwtException, SignatureException, etc.)
     * si el token es invalido o expiro. El filtro de seguridad decide como manejarlo.
     */
    public Claims validarYObtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long obtenerUsuarioId(String token) {
        return Long.valueOf(validarYObtenerClaims(token).getSubject());
    }

    // --- Refresh token: opaco, no JWT ---

    /** Genera un valor aleatorio de 256 bits, codificado en Base64 URL-safe. */
    public String generarRefreshTokenPlano() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** Hashea el refresh token en texto plano para guardarlo en BD (nunca se guarda el valor real). */
    public String hashear(String valorPlano) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(valorPlano.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 siempre esta disponible en la JVM estandar; esto no deberia ocurrir nunca.
            throw new IllegalStateException("Algoritmo de hashing no disponible", e);
        }
    }
}
