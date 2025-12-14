package com.ssafy.foofa.chat.domain.service;

import com.ssafy.foofa.chat.domain.Message;
import com.ssafy.foofa.chat.domain.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public Message createMessage(String battleId, String senderId, String content, List<String> recipientIds) {
        Message message = Message.createNew(battleId, senderId, content);

        // 수신자들을 unread로 추가
        for (String recipientId : recipientIds) {
            if (!recipientId.equals(senderId)) {
                message = message.addUnreadRecipient(recipientId);
            }
        }

        return messageRepository.save(message);
    }
}
