package com.ssafy.foofa.core.util;

import com.ssafy.foofa.core.ErrorCode;
import com.ssafy.foofa.identity.application.TokenInfo;
import com.ssafy.foofa.identity.application.TokenManager;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TokenExtractor {
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Authorization 헤더에서 JWT 토큰 추출
     */
    public static String extractToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new IllegalArgumentException(ErrorCode.TOKEN_MISSING.getMessage());
        }

        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException(ErrorCode.TOKEN_HEADER_INVALID.getMessage());
        }

        String token = authorization.substring(BEARER_PREFIX.length());

        if (token.isBlank()) {
            throw new IllegalArgumentException(ErrorCode.TOKEN_INVALID.getMessage());
        }

        return token;
    }

    public static String parseUserId(String token, TokenManager tokenManager) {
        TokenInfo tokenInfo = tokenManager.parseClaims(token);
        return tokenInfo.memberId();
    }
}
