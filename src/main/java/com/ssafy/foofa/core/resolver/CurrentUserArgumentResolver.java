package com.ssafy.foofa.core.resolver;

import com.ssafy.foofa.core.ErrorCode;
import com.ssafy.foofa.core.annotation.CurrentUser;
import com.ssafy.foofa.core.util.TokenExtractor;
import com.ssafy.foofa.identity.application.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * @CurrentUser 어노테이션 처리 ArgumentResolver (WebSocket용)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final TokenManager tokenManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && String.class.isAssignableFrom(parameter.getParameterType());
    }

    /**
     * WebSocket 메시지 헤더에서 JWT 토큰 추출 및 userId 반환
     */
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            Message<?> message
    ) {
        try {
            // WebSocket 헤더에서 Authorization 추출
            String authorization = extractAuthorizationHeader(message);

            // Authorization에서 JWT 토큰 추출
            String token = TokenExtractor.extractToken(authorization);

            // JWT 파싱 및 userId 추출
            String userId = TokenExtractor.parseUserId(token, tokenManager);

            return userId;

        } catch (Exception e) {
            log.error("Failed to resolve userId from WebSocket message", e);
            throw e;
        }
    }

    private String extractAuthorizationHeader(Message<?> message) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        String authorization = accessor.getFirstNativeHeader("Authorization");

        if (authorization == null || authorization.isBlank()) {
            throw new IllegalArgumentException(ErrorCode.TOKEN_MISSING.getMessage());
        }

        return authorization;
    }
}
