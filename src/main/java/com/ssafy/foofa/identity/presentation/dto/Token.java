package com.ssafy.foofa.identity.presentation.dto;

public record Token(
        String accessToken,
        String refreshToken
) {
}
