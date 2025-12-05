package com.ssafy.foofa.identity.domain.dto;

public record UserInfo(
        String oauthProviderId,
        String email
) {
}
