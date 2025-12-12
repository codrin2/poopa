package com.ssafy.foofa.core.dlq;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "dead_letter_messages")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeadLetterMessage {
    @Id
    private String id;

    private String originalMessage;

    private String failureReason;

    @Builder.Default
    private Integer retryCount = 0;

    private LocalDateTime failedAt;

    public static DeadLetterMessage create(
            String originalMessage,
            String failureReason,
            int retryCount
    ) {
        return DeadLetterMessage.builder()
                .id(UUID.randomUUID().toString())
                .originalMessage(originalMessage)
                .failureReason(failureReason)
                .retryCount(retryCount)
                .failedAt(LocalDateTime.now())
                .build();
    }
}
