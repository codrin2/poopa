package com.ssafy.foofa.chat.infra;

import com.ssafy.foofa.chat.presentation.dto.event.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CHANNEL_PREFIX = "chat.battle.";

    public void publishMessage(ChatMessageEvent event) {
        String channel = CHANNEL_PREFIX + event.getBattleId();
        redisTemplate.convertAndSend(channel, event);
    }
}
