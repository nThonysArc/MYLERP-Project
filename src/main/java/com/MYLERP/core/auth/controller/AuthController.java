package com.MYLERP.core.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MYLERP.core.auth.dto.LoginRequest;
import com.MYLERP.core.auth.dto.RefreshRequest;
import com.MYLERP.core.auth.dto.TokenResponse;
import com.MYLERP.core.auth.service.AuthService;
import com.MYLERP.core.usuario.dto.RegistroRequest;
import com.MYLERP.core.usuario.dto.UsuarioDTO;
import com.MYLERP.core.usuario.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registro(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refrescar(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-todos")
    public ResponseEntity<Void> logoutDeTodosLosDispositivos(Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        authService.logoutDeTodosLosDispositivos(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
