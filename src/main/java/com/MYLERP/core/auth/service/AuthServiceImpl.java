package com.MYLERP.core.auth.service;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.MYLERP.core.auth.dto.LoginRequest;
import com.MYLERP.core.auth.dto.TokenResponse;
import com.MYLERP.core.auth.model.RefreshToken;
import com.MYLERP.core.auth.repository.RefreshTokenRepository;
import com.MYLERP.core.usuario.model.Usuario;
import com.MYLERP.core.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token-expiration-days}")
    private long refreshTokenExpirationDays;

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        return emitirTokens(usuario, request.dispositivo());
    }

    @Override
    @Transactional
    public TokenResponse refrescar(String refreshTokenPlano) {
        String hash = jwtService.hashear(refreshTokenPlano);

        RefreshToken almacenado = refreshTokenRepository.findByTokenHashAndRevocadoFalse(hash)
                .orElseThrow(() -> new BadCredentialsException("Refresh token invalido o revocado"));

        if (almacenado.getExpiraEn().isBefore(OffsetDateTime.now())) {
            throw new BadCredentialsException("Refresh token expirado, inicia sesion nuevamente");
        }

        // Rotacion: el refresh token usado se revoca y se emite uno nuevo.
        // Si alguien roba un refresh token viejo y lo reintenta, esto ya no valida (ya esta revocado).
        almacenado.setRevocado(true);
        refreshTokenRepository.save(almacenado);

        return emitirTokens(almacenado.getUsuario(), almacenado.getDispositivo());
    }

    @Override
    @Transactional
    public void logout(String refreshTokenPlano) {
        String hash = jwtService.hashear(refreshTokenPlano);
        refreshTokenRepository.findByTokenHashAndRevocadoFalse(hash)
                .ifPresent(token -> {
                    token.setRevocado(true);
                    refreshTokenRepository.save(token);
                });
    }

    @Override
    @Transactional
    public void logoutDeTodosLosDispositivos(Long usuarioId) {
        refreshTokenRepository.revocarTodosDelUsuario(usuarioId);
    }

    private TokenResponse emitirTokens(Usuario usuario, String dispositivo) {
        String accessToken = jwtService.generarAccessToken(usuario.getId(), usuario.getEmail());

        String refreshTokenPlano = jwtService.generarRefreshTokenPlano();
        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .tokenHash(jwtService.hashear(refreshTokenPlano))
                .dispositivo(dispositivo)
                .expiraEn(OffsetDateTime.now().plusDays(refreshTokenExpirationDays))
                .build();
        refreshTokenRepository.save(refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenPlano) // el plano SOLO se entrega aqui, una vez; en BD solo queda el hash
                .tokenType("Bearer")
                .expiraEnSegundos(jwtService.getAccessTokenExpirationSeconds())
                .build();
    }
}
