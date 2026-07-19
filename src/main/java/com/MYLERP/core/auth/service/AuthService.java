package com.MYLERP.core.auth.service;

import com.MYLERP.core.auth.dto.LoginRequest;
import com.MYLERP.core.auth.dto.TokenResponse;

public interface AuthService {

    TokenResponse login(LoginRequest request);

    TokenResponse refrescar(String refreshTokenPlano);

    void logout(String refreshTokenPlano);

    // Revoca TODAS las sesiones del usuario (ej. boton "cerrar sesion en todos los dispositivos").
    void logoutDeTodosLosDispositivos(Long usuarioId);
}
