package com.ssafy.foofa.core.dlq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DLQHandler {
    private static final String DLQ_KEY = "chat:dlq:messages";
    private static final long DLQ_TTL_DAYS = 7;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 재시도 불가능한 에러로 인한 메시지를 DLQ에 저장
     *
     * @param originalMessage 원본 메시지 (byte[])
     * @param failureReason 실패 사유
     */
    public void sendToDeadLetterQueue(
            byte[] originalMessage,
            String failureReason
    ) {
        try {
            String messageContent = new String(originalMessage);
            DeadLetterMessage dlqMessage = DeadLetterMessage.create(
                    messageContent,
                    failureReason,
                    0
            );

            saveToRedis(dlqMessage);

            log.error("Message sent to DLQ - messageId: {}, reason: {}", dlqMessage.getId(), failureReason);

        } catch (Exception e) {
            log.error("Failed to send message to DLQ - error: {}", e.getMessage(), e);
        }
    }

    /**
     * 재시도 실패로 인한 메시지를 DLQ에 저장
     *
     * @param originalMessage 원본 메시지 (byte[])
     * @param retryCount 재시도 횟수
     */
    public void sendToDeadLetterQueueAfterRetries(
            byte[] originalMessage,
            int retryCount
    ) {
        try {
            String messageContent = new String(originalMessage);
            String failureReason = "Max retry attempts exceeded: " + retryCount;

            DeadLetterMessage dlqMessage = DeadLetterMessage.create(
                    messageContent,
                    failureReason,
                    retryCount
            );

            saveToRedis(dlqMessage);

            log.error("Message sent to DLQ after retries - messageId: {}, retryCount: {}", dlqMessage.getId(), retryCount);

        } catch (Exception e) {
            log.error("Failed to send message to DLQ after retries - error: {}", e.getMessage(), e);
        }
    }

    private void saveToRedis(DeadLetterMessage dlqMessage) throws JsonProcessingException {
        String jsonMessage = objectMapper.writeValueAsString(dlqMessage);

        // Redis List의 왼쪽(앞)에 추가
        redisTemplate.opsForList().leftPush(DLQ_KEY, jsonMessage);

        // TTL 설정 (7일)
        redisTemplate.expire(DLQ_KEY, DLQ_TTL_DAYS, TimeUnit.DAYS);
    }

    public Long getDlqMessageCount() {
        return redisTemplate.opsForList().size(DLQ_KEY);
    }
}
