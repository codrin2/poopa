package com.ssafy.foofa.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    private String content;

    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    @Builder.Default
    private Map<String, LocalDateTime> readBy = new HashMap<>();

    private LocalDateTime createdAt;

    /**
     * 일반 텍스트 메시지 생성
     */
    public static Message createTextMessage(String battleId, String senderId, String content,
                                            String userId1, String userId2) {
        Map<String, LocalDateTime> readBy = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        readBy.put(userId1, userId1.equals(senderId) ? now : null);
        readBy.put(userId2, userId2.equals(senderId) ? now : null);

        return Message.builder()
                .battleId(battleId)
                .senderId(senderId)
                .type(MessageType.TEXT)
                .content(content)
                .readBy(readBy)
                .createdAt(now)
                .build();
    }

    /**
     * 식사 인증 시스템 메시지 생성
     */
    public static Message createCheckInSystemMessage(String battleId, String userId, String userName,
                                                     String checkInId, int score, String mealTime,
                                                     String opponentUserId) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("checkInId", checkInId);
        metadata.put("score", score);
        metadata.put("mealTime", mealTime);
        metadata.put("userName", userName);

        Map<String, LocalDateTime> readBy = new HashMap<>();
        readBy.put(userId, null);
        readBy.put(opponentUserId, null);

        String content = String.format("%s님이 %s 식사를 인증했습니다. (점수: %d점)",
                userName, mealTime, score);

        return Message.builder()
                .battleId(battleId)
                .senderId(null)
                .type(MessageType.SYSTEM_CHECK_IN)
                .content(content)
                .metadata(metadata)
                .readBy(readBy)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 치팅데이 사용 시스템 메시지 생성
     */
    public static Message createCheatDaySystemMessage(String battleId, String userId, String userName,
                                                      String mealTime, String opponentUserId) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("mealTime", mealTime);
        metadata.put("userName", userName);

        Map<String, LocalDateTime> readBy = new HashMap<>();
        readBy.put(userId, null);
        readBy.put(opponentUserId, null);

        String content = String.format("%s님이 %s 치팅데이를 사용했습니다.",
                userName, mealTime);

        return Message.builder()
                .battleId(battleId)
                .senderId(null)
                .type(MessageType.SYSTEM_CHEAT_DAY)
                .content(content)
                .metadata(metadata)
                .readBy(readBy)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 메시지 읽음 처리 (새 인스턴스 반환)
     */
    public Message markAsRead(String userId) {
        if (!this.readBy.containsKey(userId)) {
            throw new IllegalArgumentException("User not in readBy map");
        }

        Map<String, LocalDateTime> updatedReadBy = new HashMap<>(this.readBy);
        updatedReadBy.put(userId, LocalDateTime.now());

        return Message.builder()
                .id(this.id)
                .battleId(this.battleId)
                .senderId(this.senderId)
                .type(this.type)
                .content(this.content)
                .metadata(this.metadata)
                .readBy(updatedReadBy)
                .createdAt(this.createdAt)
                .build();
    }

    /**
     * 특정 사용자가 읽었는지 확인
     */
    public boolean isReadBy(String userId) {
        return this.readBy.get(userId) != null;
    }

    /**
     * 시스템 메시지인지 확인
     */
    public boolean isSystemMessage() {
        return this.senderId == null;
    }
}
