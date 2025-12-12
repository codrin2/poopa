package com.ssafy.foofa.chat.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.foofa.chat.application.ChatMessageProcessor;
import com.ssafy.foofa.chat.presentation.dto.event.ChatMessageEvent;
import com.ssafy.foofa.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber {
    private static final String CHANNEL_PREFIX = "chat.battle.";

    private final ChatMessageProcessor messageProcessor;
    private final DeadLetterQueueService dlqService;
    private final ObjectMapper objectMapper;

    /**
     * Redis 메시지 수신 및 처리
     * 1. 검증 (Non-retryable) → 실패 시 즉시 DLQ
     * 2. 역직렬화 (Non-retryable) → 실패 시 즉시 DLQ
     * 3. 메시지 처리 (Retryable) → ChatMessageProcessor로 위임
     */
    public void onMessage(Message redisMessage, byte[] pattern) {
        String channel = null;
        String battleId = null;

        try {
            // 메시지 검증 (Non-retryable)
            validateMessage(redisMessage);
            channel = new String(redisMessage.getChannel());
            battleId = extractBattleId(channel);

            // 메시지 역직렬화 (Non-retryable)
            ChatMessageEvent event = deserializeMessage(redisMessage, channel);

            // 메시지 처리 (Retryable - processor에서 재시도 처리)
            messageProcessor.processMessage(
                    channel,
                    battleId,
                    event,
                    redisMessage.getBody()
            );

        } catch (IllegalArgumentException e) {
            // Non-retryable: 메시지 검증 실패 → 즉시 DLQ
            log.error("Validation failed - channel: {}, error: {}", channel, e.getMessage());
            dlqService.sendToDeadLetterQueue(
                    channel,
                    battleId,
                    redisMessage != null ? redisMessage.getBody() : new byte[0],
                    e.getMessage(),
                    e
            );

        } catch (JsonProcessingException e) {
            // Non-retryable: 역직렬화 실패 → 즉시 DLQ
            logDeserializationError(redisMessage, channel, e);
            dlqService.sendToDeadLetterQueue(
                    channel,
                    battleId,
                    redisMessage.getBody(),
                    ErrorCode.REDIS_MESSAGE_DESERIALIZATION_FAILED.format(channel),
                    e
            );

        } catch (MessagingException e) {
            // Retryable: 이미 processor에서 재시도 완료 후 실패
            // @Recover에서 DLQ 처리되므로 여기서는 로그만
            log.error("Message processing failed after all retries - channel: {}, battleId: {}", channel, battleId);

        } catch (Exception e) {
            // 예상치 못한 오류 → DLQ
            log.error("Unexpected error - channel: {}, battleId: {}", channel, battleId, e);
            dlqService.sendToDeadLetterQueue(
                    channel,
                    battleId,
                    redisMessage != null ? redisMessage.getBody() : new byte[0],
                    "Unexpected error: " + e.getMessage(),
                    e
            );
        }
    }

    private void validateMessage(Message redisMessage) {
        if (redisMessage == null) {
            throw new IllegalArgumentException(ErrorCode.REDIS_MESSAGE_NULL.getMessage());
        }

        if (redisMessage.getBody() == null || redisMessage.getBody().length == 0) {
            throw new IllegalArgumentException(ErrorCode.REDIS_MESSAGE_EMPTY.getMessage());
        }

        if (redisMessage.getChannel() == null || redisMessage.getChannel().length == 0) {
            throw new IllegalArgumentException(ErrorCode.REDIS_CHANNEL_INVALID.format("null or empty"));
        }
    }

    private String extractBattleId(String channel) {
        if (!channel.startsWith(CHANNEL_PREFIX)) {
            throw new IllegalArgumentException(ErrorCode.REDIS_CHANNEL_INVALID.format(channel));
        }

        String battleId = channel.substring(CHANNEL_PREFIX.length());

        if (battleId.isEmpty()) {
            throw new IllegalArgumentException(ErrorCode.REDIS_CHANNEL_INVALID.format(channel));
        }

        return battleId;
    }

    /**
     * Redis 메시지를 ChatMessageEvent로 역직렬화
     */
    private ChatMessageEvent deserializeMessage(Message redisMessage, String channel) throws JsonProcessingException {
        try {
            return objectMapper.readValue(
                    redisMessage.getBody(),
                    ChatMessageEvent.class
            );
        } catch (IOException e) {
            throw new JsonProcessingException(ErrorCode.REDIS_MESSAGE_DESERIALIZATION_FAILED.format(channel)) {};
        }
    }

    /**
     * 역직렬화 오류 상세 로깅 (디버깅 및 모니터링용)
     */
    private void logDeserializationError(Message redisMessage, String channel, JsonProcessingException e) {
        String rawMessage = new String(redisMessage.getBody());
        log.error("Message deserialization failed - channel: {}, rawMessage: {}, error: {}",
                channel,
                rawMessage.length() > 500 ? rawMessage.substring(0, 500) + "..." : rawMessage,
                e.getMessage());
    }
}
