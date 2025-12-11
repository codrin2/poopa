package com.ssafy.foofa.chat.presentation;

import com.ssafy.foofa.chat.application.ChatFacade;
import com.ssafy.foofa.chat.presentation.dto.request.SendMessageRequest;
import com.ssafy.foofa.chat.presentation.swagger.ChatSwagger;
import com.ssafy.foofa.core.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController implements ChatSwagger {
    private final ChatFacade chatFacade;

    @Override
    @MessageMapping("/chat/message")
    public void sendMessage(
            @CurrentUser String userId,
            SendMessageRequest request
    ) {
        chatFacade.sendMessage(
                request.getBattleId(),
                userId,
                request.getContent()
        );
    }
}
