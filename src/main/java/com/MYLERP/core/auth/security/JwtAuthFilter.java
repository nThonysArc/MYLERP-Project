package com.MYLERP.core.auth.security;

import java.io.IOException;
import java.util.Collections;

import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.MYLERP.core.auth.service.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest

;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Se ejecuta en cada request. Si hay un Bearer token valido, autentica al usuario
 * en el SecurityContext (sin sesion: el token ES la prueba de identidad en cada llamada).
 * Si no hay token, o es invalido, simplemente continua la cadena sin autenticar;
 * es SecurityConfig quien decide si esa ruta requiere autenticacion o no.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith(PREFIJO_BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(PREFIJO_BEARER.length());

        try {
            Long usuarioId = jwtService.obtenerUsuarioId(token);

            var authentication = new UsernamePasswordAuthenticationToken(
                    usuarioId, // principal: el id del usuario, simple y suficiente para el resto del backend
                    null,
                    Collections.emptyList() // sin roles por ahora; se agregan cuando haya RBAC
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException ex) {
            // Token invalido/expirado: no autenticamos, dejamos que SecurityConfig
            // devuelva 401 si la ruta lo requiere. No lanzamos excepcion aqui.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
