package com.ssafy.foofa.core.outbox;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "outbox_events")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OutboxEvent {
    private static final int MAX_RETRY_COUNT = 3;

    @Id
    private String id;

    private String eventType;

    private String payload;

    @Indexed
    private OutboxStatus status;

    @Builder.Default
    private Integer retryCount = 0;

    private LocalDateTime createdAt;

    public static OutboxEvent create(String eventType, String payload) {
        return OutboxEvent.builder()
                // .id() 제거! MongoDB가 자동 생성
                .eventType(eventType)
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public OutboxEvent markAsPublished() {
        return OutboxEvent.builder()
                .id(this.id)
                .eventType(this.eventType)
                .payload(this.payload)
                .status(OutboxStatus.PUBLISHED)
                .retryCount(this.retryCount)
                .createdAt(this.createdAt)
                .build();
    }

    public OutboxEvent incrementRetry() {
        return OutboxEvent.builder()
                .id(this.id)
                .eventType(this.eventType)
                .payload(this.payload)
                .status(this.status)
                .retryCount(this.retryCount + 1)
                .createdAt(this.createdAt)
                .build();
    }

    public OutboxEvent markAsFailed() {
        return OutboxEvent.builder()
                .id(this.id)
                .eventType(this.eventType)
                .payload(this.payload)
                .status(OutboxStatus.FAILED)
                .retryCount(this.retryCount)
                .createdAt(this.createdAt)
                .build();
    }

    public boolean canRetry() {
        return this.retryCount < MAX_RETRY_COUNT;
    }
}
