package com.ssafy.foofa.core;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TokenExtractor {
    private static final String BEARER_PREFIX = "Bearer ";

    public static String extract(String header) {
        if (header == null) {
            throw new IllegalArgumentException(ErrorCode.TOKEN_MISSING.getMessage());
        }
        if (!header.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException(ErrorCode.TOKEN_HEADER_INVALID.getMessage());
        }
        return header.substring(BEARER_PREFIX.length());
    }
}
