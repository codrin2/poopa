package com.ssafy.foofa.core.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.foofa.chat.presentation.dto.event.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventScheduler {
    private static final String CHANNEL_PREFIX = "chat.battle.";
    private static final int BATCH_SIZE = 100;

    private final OutboxPublisher outboxPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Outbox 이벤트 polling 및 발행
     */
    @Scheduled(fixedDelay = 1000)
    public void processOutboxEvents() {
        try {
            List<OutboxEvent> pendingEvents = outboxPublisher.findPendingEvents(BATCH_SIZE);

            if (pendingEvents.isEmpty()) {
                return;
            }

            for (OutboxEvent event : pendingEvents) {
                try {
                    publishToRedis(event);
                    outboxPublisher.markAsPublished(event);

                } catch (Exception e) {
                    handlePublishFailure(event, e);
                }
            }

        } catch (Exception e) {
            log.error("Outbox event scheduler error", e);
        }
    }

    private void publishToRedis(OutboxEvent event) throws Exception {
        ChatMessageEvent chatEvent = objectMapper.readValue(
                event.getPayload(),
                ChatMessageEvent.class
        );

        String channel = CHANNEL_PREFIX + chatEvent.battleId();

        redisTemplate.convertAndSend(channel, chatEvent);
    }

    private void handlePublishFailure(OutboxEvent event, Exception e) {
        if (event.canRetry()) {
            outboxPublisher.incrementRetry(event);

        } else {
            outboxPublisher.markAsFailed(event);
            log.error("Outbox event publish failed permanently - eventId: {}, error: {}", event.getId(), e.getMessage());
        }
    }
}
