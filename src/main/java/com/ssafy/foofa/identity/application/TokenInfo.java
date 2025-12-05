package com.ssafy.foofa.identity.application;

import java.time.Instant;

public record TokenInfo(
        String tokenId,
        String memberId,
        Instant expiration
) {
}
