package com.ssafy.foofa.core.resolver;

import com.ssafy.foofa.core.ErrorCode;
import com.ssafy.foofa.core.annotation.CurrentUser;
import com.ssafy.foofa.core.util.TokenExtractor;
import com.ssafy.foofa.identity.application.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @CurrentUser 어노테이션 처리 ArgumentResolver (HTTP용)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpCurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final TokenManager tokenManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && String.class.isAssignableFrom(parameter.getParameterType());
    }

    /**
     * HTTP 요청 헤더에서 JWT 토큰 추출 및 userId 반환
     */
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        try {
            // HTTP 요청 헤더에서 Authorization 추출
            String authorization = extractAuthorizationHeader(webRequest);

            // Authorization에서 JWT 토큰 추출
            String token = TokenExtractor.extractToken(authorization);

            // JWT 파싱 및 userId 추출
            String userId = TokenExtractor.parseUserId(token, tokenManager);

            return userId;

        } catch (Exception e) {
            log.error("Failed to resolve userId from HTTP request", e);
            throw e;
        }
    }

    private String extractAuthorizationHeader(NativeWebRequest webRequest) {
        String authorization = webRequest.getHeader("Authorization");

        if (authorization == null || authorization.isBlank()) {
            throw new IllegalArgumentException(ErrorCode.TOKEN_MISSING.getMessage());
        }

        return authorization;
    }
}
