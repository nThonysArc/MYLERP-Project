package com.MYLERP.core.auth.dto;

import lombok.Builder;

@Builder
public record TokenResponse (
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiraEnSegundos
) {}