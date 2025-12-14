package com.ssafy.foofa.core.config;

import com.ssafy.foofa.core.util.TokenExtractor;
import com.ssafy.foofa.identity.application.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final TokenManager tokenManager;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트로 메시지 전송하는 prefix
        registry.enableSimpleBroker("/topic");

        // 클라이언트에서 메시지 전송하는 prefix
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * WebSocket Handshake 시 인증 처리 (CONNECT 시점에만)
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // CONNECT 시에만 인증 수행 (최초 1회)
                    String authorization = accessor.getFirstNativeHeader("Authorization");

                    try {
                        String token = TokenExtractor.extractToken(authorization);
                        String userId = TokenExtractor.parseUserId(token, tokenManager);

                        // 세션에 userId 저장 (이후 메시지에서 재사용)
                        accessor.getSessionAttributes().put("userId", userId);

                    } catch (Exception e) {
                        log.error("WebSocket authentication failed: {}", e.getMessage());
                        throw e;
                    }
                }

                return message;
            }
        });
    }
}
