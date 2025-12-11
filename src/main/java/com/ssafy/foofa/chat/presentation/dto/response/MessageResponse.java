package com.ssafy.foofa.chat.presentation.dto.response;

import com.ssafy.foofa.chat.domain.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String id;
    private String battleId;
    private String senderId;
    private String content;
    private LocalDateTime createdAt;

    public static MessageResponse from(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .battleId(message.getBattleId())
                .senderId(message.getSenderId())
                .content(message.getContent().getValue())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
