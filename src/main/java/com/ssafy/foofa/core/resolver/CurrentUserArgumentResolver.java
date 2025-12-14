package com.ssafy.foofa.core.resolver;

import com.ssafy.foofa.core.ErrorCode;
import com.ssafy.foofa.core.annotation.CurrentUser;
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
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && String.class.isAssignableFrom(parameter.getParameterType());
    }

    /**
     * WebSocket 세션에서 userId 추출 (토큰 재검증 없음)
     */
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            Message<?> message
    ) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);

        // CONNECT 시점에 저장한 userId 조회
        String userId = (String) accessor.getSessionAttributes().get("userId");

        if (userId == null) {
            log.error("userId not found in WebSocket session - sessionId: {}",
                    accessor.getSessionId());
            throw new IllegalArgumentException(ErrorCode.TOKEN_INVALID.getMessage());
        }

        return userId;
    }
}
