package com.ssafy.foofa.identity.presentation.dto;

public record LoginResponse(
        String userId,
        String accessToken,
        String refreshToken
) {
    public static LoginResponse of(String userId, String accessToken, String refreshToken) {
        return new LoginResponse(userId, accessToken, refreshToken);
    }
}
