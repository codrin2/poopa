package com.ssafy.foofa.chat.infra;

import com.ssafy.foofa.chat.application.event.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketMessageSender {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessageToBattleMembers(String battleId, ChatMessageEvent event) {
        String destination = "/topic/battle/" + battleId;
        messagingTemplate.convertAndSend(destination, event);
    }
}
