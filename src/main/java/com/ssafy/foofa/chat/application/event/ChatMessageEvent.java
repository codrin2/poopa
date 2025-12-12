package com.ssafy.foofa.chat.application.event;

import com.ssafy.foofa.chat.domain.Message;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ChatMessageEvent(
        String type,
        String battleId,
        MessageData message
) implements Serializable {

    public record MessageData(
            String id,
            String battleId,
            String senderId,
            String content,
            LocalDateTime createdAt
    ) implements Serializable {
    }

    public static ChatMessageEvent from(Message message) {
        return new ChatMessageEvent(
                "MESSAGE",
                message.getBattleId(),
                new MessageData(
                        message.getId(),
                        message.getBattleId(),
                        message.getSenderId(),
                        message.getContent().getValue(),
                        message.getCreatedAt()
                )
        );
    }
}
