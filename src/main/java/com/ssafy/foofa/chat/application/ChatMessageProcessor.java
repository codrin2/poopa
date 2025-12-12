package com.ssafy.foofa.chat.application;

import com.ssafy.foofa.chat.infra.WebSocketMessageSender;
import com.ssafy.foofa.chat.application.event.ChatMessageEvent;
import com.ssafy.foofa.core.ErrorCode;
import com.ssafy.foofa.core.dlq.DLQHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageProcessor {
    private static final int MAX_RETRY_ATTEMPTS = 3;

    private final WebSocketMessageSender messageSender;
    private final DLQHandler dlqHandler;

    /**
     * WebSocket으로 메시지 전송 (재시도 가능)
     *
     * @param channel Redis 채널명
     * @param battleId 대결 ID
     * @param event 채팅 메시지 이벤트
     * @param originalMessageBytes 원본 메시지 (DLQ 저장용)
     * @throws MessagingException WebSocket 전송 실패 시 (재시도 대상)
     */
    @Retryable(
            retryFor = {MessagingException.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2.0,
                    maxDelay = 5000
            )
    )
    public void processMessage(
            String channel,
            String battleId,
            ChatMessageEvent event,
            byte[] originalMessageBytes
    ) throws MessagingException {
        try {
            messageSender.sendMessageToBattleMembers(battleId, event);

        } catch (Exception e) {
            log.warn("WebSocket send failed - channel: {}, battleId: {}, will retry. error: {}",
                    channel, battleId, e.getMessage());

            throw new MessagingException(
                    ErrorCode.WEBSOCKET_MESSAGE_SEND_FAILED.format(battleId),
                    e
            );
        }
    }

    /**
     * 재시도 실패 후 복구 메서드
     * 최대 재시도 횟수 초과 시 DLQ로 전송
     *
     * @param e 마지막 발생한 예외
     * @param channel Redis 채널명
     * @param battleId 대결 ID
     * @param event 채팅 메시지 이벤트
     * @param originalMessageBytes 원본 메시지
     */
    @Recover
    public void recoverFromRetryFailure(
            MessagingException e,
            String channel,
            String battleId,
            ChatMessageEvent event,
            byte[] originalMessageBytes
    ) {
        log.error("Message processing failed after {} retries - channel: {}, battleId: {}", MAX_RETRY_ATTEMPTS, channel, battleId);

        // DLQ로 전송
        dlqHandler.sendToDeadLetterQueueAfterRetries(
                originalMessageBytes,
                MAX_RETRY_ATTEMPTS
        );
    }
}
