package com.ssafy.foofa.core.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.foofa.chat.application.event.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveOutboxEvent(ChatMessageEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = OutboxEvent.create("CHAT_MESSAGE_CREATED", payload);

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ChatMessageEvent", e);
            throw new IllegalStateException("Failed to save outbox event", e);
        }
    }

    /**
     * PENDING 상태의 이벤트 조회 (polling용)
     */
    @Transactional(readOnly = true)
    public List<OutboxEvent> findPendingEvents(int limit) {
        return outboxEventRepository.findByStatusOrderByCreatedAtAsc(
                OutboxStatus.PENDING,
                PageRequest.of(0, limit)
        );
    }

    @Transactional
    public void markAsPublished(OutboxEvent event) {
        OutboxEvent published = event.markAsPublished();
        outboxEventRepository.save(published);
    }

    @Transactional
    public void incrementRetry(OutboxEvent event) {
        OutboxEvent retried = event.incrementRetry();
        outboxEventRepository.save(retried);
    }

    @Transactional
    public void markAsFailed(OutboxEvent event) {
        OutboxEvent failed = event.markAsFailed();
        outboxEventRepository.save(failed);

        log.error("Outbox event marked as failed - eventId: {}", event.getId());
    }
}
