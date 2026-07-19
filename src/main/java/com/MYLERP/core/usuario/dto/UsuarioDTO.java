package com.MYLERP.core.usuario.dto;

import lombok.Builder;

// Nunca incluye passwordHash: es el contrato publico hacia el frontend.
@Builder
public record UsuarioDTO (
        Long id,
        String email,
        String nombre,
        String zonaHoraria
){}
