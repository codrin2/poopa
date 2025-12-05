package com.ssafy.foofa.identity.infra.dto;

public record KakaoUserInfoResponse(
        Long id,
        KakaoAccount kakao_account
) {
    public record KakaoAccount(
            String email
    ) {
    }
}
