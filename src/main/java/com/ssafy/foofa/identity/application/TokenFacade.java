package com.ssafy.foofa.identity.application;

import com.ssafy.foofa.core.JwtProperties;
import com.ssafy.foofa.identity.presentation.dto.Token;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenFacade {
    public static final long HOURS_IN_MILLIS = 60 * 60 * 1000L;

    private final JwtProperties jwtProperties;
    private final TokenManager tokenManager;
    private long accessTokenTime;
    private long refreshTokenTime;

    @PostConstruct
    public void init() {
        this.accessTokenTime = jwtProperties.accessTokenExpireTimeInHours() * HOURS_IN_MILLIS;
        this.refreshTokenTime = jwtProperties.refreshTokenExpireTimeInHours() * HOURS_IN_MILLIS;
    }

    public Token issue(String memberId) {
        String newAccessToken = tokenManager.createToken(memberId, accessTokenTime);
        String newRefreshToken = tokenManager.createToken(memberId, refreshTokenTime);

        return new Token(newAccessToken, newRefreshToken);
    }

    public Token reissue(String oldRefreshToken) {
        TokenInfo tokenInfo = tokenManager.parseClaimsFromRefreshToken(oldRefreshToken);
        String memberId = tokenInfo.memberId();

        return issue(memberId);
    }
}
