package com.ssafy.foofa.chat.domain;

import com.ssafy.foofa.core.annotation.AggregateRoot;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@AggregateRoot
@Document(collection = "messages")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Message {
    @Id
    private String id;

    @Indexed
    private String battleId;

    private String senderId;

    private MessageType type;

    private MessageContent content;

    @Builder.Default
    private Map<String, ReadReceipt> readReceipts = new HashMap<>();

    private LocalDateTime createdAt;

    public static Message createNew(String battleId, String senderId, String content) {
        return Message.builder()
                .battleId(battleId)
                .senderId(senderId)
                .type(MessageType.TEXT)
                .content(MessageContent.of(content))
                .createdAt(LocalDateTime.now())
                .readReceipts(initializeReadReceipts(senderId))
                .build();
    }

    /**
     * 읽음 상태 초기화 (발신자는 읽음 처리)
     */
    private static Map<String, ReadReceipt> initializeReadReceipts(String senderId) {
        Map<String, ReadReceipt> receipts = new HashMap<>();
        receipts.put(senderId, ReadReceipt.createRead(senderId));
        return receipts;
    }

    /**
     * 메시지 읽음 처리
     */
    public Message markAsReadBy(String userId) {
        if (readReceipts.containsKey(userId) && readReceipts.get(userId).isRead()) {
            return this;  // 이미 읽음
        }

        Map<String, ReadReceipt> updatedReceipts = new HashMap<>(this.readReceipts);
        updatedReceipts.put(userId, ReadReceipt.createRead(userId));

        return Message.builder()
                .id(this.id)
                .battleId(this.battleId)
                .senderId(this.senderId)
                .type(this.type)
                .content(this.content)
                .readReceipts(updatedReceipts)
                .createdAt(this.createdAt)
                .build();
    }

    /**
     * 특정 사용자가 읽었는지 확인
     */
    public boolean isReadBy(String userId) {
        ReadReceipt receipt = readReceipts.get(userId);
        return receipt != null && receipt.isRead();
    }

    /**
     * 수신자를 안읽음으로 추가
     */
    public Message addUnreadRecipient(String userId) {
        if (readReceipts.containsKey(userId)) {
            return this;
        }

        Map<String, ReadReceipt> updatedReceipts = new HashMap<>(this.readReceipts);
        updatedReceipts.put(userId, ReadReceipt.createUnread(userId));

        return Message.builder()
                .id(this.id)
                .battleId(this.battleId)
                .senderId(this.senderId)
                .type(this.type)
                .content(this.content)
                .readReceipts(updatedReceipts)
                .createdAt(this.createdAt)
                .build();
    }
}
