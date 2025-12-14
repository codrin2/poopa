package com.ssafy.foofa.chat.application;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.service.BattleService;
import com.ssafy.foofa.chat.domain.Message;
import com.ssafy.foofa.chat.domain.service.ChatPermissionService;
import com.ssafy.foofa.chat.domain.service.MessageService;
import com.ssafy.foofa.chat.application.event.ChatMessageEvent;
import com.ssafy.foofa.core.outbox.OutboxPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatFacade {
    private final MessageService messageService;
    private final ChatPermissionService permissionService;
    private final BattleService battleService;
    private final OutboxPublisher outboxPublisher;

    @Transactional
    public void sendMessage(String battleId, String senderId, String content) {
        // Battle 조회 및 권한 검증
        Battle battle = battleService.validateAndGetBattle(battleId);
        permissionService.validateSendPermission(battle, senderId);

        // 메시지 생성 및 저장
        List<String> memberIds = battle.getMembers().stream()
                .map(Battle.Member::getUserId)
                .toList();
        Message message = messageService.createMessage(battleId, senderId, content, memberIds);

        // Outbox 이벤트 저장
        ChatMessageEvent event = ChatMessageEvent.from(message);
        outboxPublisher.saveOutboxEvent(event);
    }
}
