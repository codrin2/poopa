package com.ssafy.foofa.core.interceptor;

import com.ssafy.foofa.core.ErrorCode;
import com.ssafy.foofa.identity.application.TokenInfo;
import com.ssafy.foofa.identity.application.TokenManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenManager tokenManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            try {
                TokenInfo tokenInfo = tokenManager.parseClaims(token);
                request.setAttribute("userId", tokenInfo.memberId());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(ErrorCode.TOKEN_INVALID.getMessage());
            }
        }
        return true;
    }
}
