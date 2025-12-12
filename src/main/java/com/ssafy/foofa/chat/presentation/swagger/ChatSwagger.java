package com.ssafy.foofa.chat.presentation.swagger;

import com.ssafy.foofa.chat.presentation.dto.SendMessageRequest;
import com.ssafy.foofa.core.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.handler.annotation.MessageMapping;

@Tag(
        name = "Chat WebSocket API",
        description = """
        실시간 채팅 WebSocket API

        **연결 정보**
        - Endpoint: ws://localhost:8080/ws (개발) / wss://api.foofa.com/ws (운영)
        - Protocol: STOMP over WebSocket
        - 인증: WebSocket 연결 시 Authorization 헤더에 JWT Access Token 포함 필수

        **메시지 흐름**
        1. 클라이언트 → /app/chat/message (메시지 전송)
        2. 서버 → JWT 토큰 검증 및 권한 확인
        3. 서버 → 메시지 저장 (MongoDB) + Outbox 이벤트 생성
        4. 서버 → /topic/chat/battle/{battleId} (브로드캐스트)

        **구독/발행 주소**
        - Subscribe: /topic/chat/battle/{battleId}
        - Publish: /app/chat/message
        """
)
public interface ChatSwagger {

    /*──────────────────────────────────────────────────────
     * 1. 메시지 전송
     *──────────────────────────────────────────────────────*/
    @Operation(
            summary = "채팅 메시지 전송",
            description = """
            대결방에 채팅 메시지를 전송합니다.

            - JWT 토큰에서 자동으로 userId 추출 (@CurrentUser)
            - 메시지는 MongoDB에 저장되고 Outbox 패턴으로 Redis Pub/Sub을 통해 브로드캐스트
            - 같은 대결방의 모든 참여자가 실시간으로 메시지 수신

            **처리 흐름**
            1. 클라이언트 → /app/chat/message로 메시지 전송
            2. 서버 → JWT에서 userId 추출 및 권한 검증
            3. 메시지 저장 (MongoDB) + Outbox 이벤트 생성 (트랜잭션 보장)
            4. Outbox Scheduler → Redis로 이벤트 발행
            5. /topic/chat/battle/{battleId} 구독자 전체에게 브로드캐스트
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "메시지 전송 성공 (void 반환)",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (메시지 검증 실패)",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "MESSAGE_CONTENT_EMPTY",
                                                    summary = "메시지 내용 없음",
                                                    value = """
                                {
                                  "errorCode": "MESSAGE_CONTENT_EMPTY",
                                  "message": "메시지 내용은 비어있을 수 없습니다."
                                }"""
                                            ),
                                            @ExampleObject(
                                                    name = "MESSAGE_CONTENT_TOO_LONG",
                                                    summary = "메시지 길이 초과",
                                                    value = """
                                {
                                  "errorCode": "MESSAGE_CONTENT_TOO_LONG",
                                  "message": "메시지 내용이 최대 길이(1000자)를 초과했습니다."
                                }"""
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 오류 (토큰 검증 실패)",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "TOKEN_MISSING",
                                                    summary = "Authorization 헤더 없음",
                                                    value = """
                                {
                                  "errorCode": "TOKEN_MISSING",
                                  "message": "인증 토큰이 필요합니다. Authorization 헤더를 확인해 주세요."
                                }"""
                                            ),
                                            @ExampleObject(
                                                    name = "TOKEN_EXPIRED",
                                                    summary = "Access Token 만료",
                                                    value = """
                                {
                                  "errorCode": "TOKEN_EXPIRED",
                                  "message": "액세스 토큰이 만료되었습니다. 토큰을 재발급 받아주세요."
                                }"""
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없음 (대결방 참여자 아님)",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                {
                                  "errorCode": "BATTLE_MEMBER_NOT_FOUND",
                                  "message": "대결 참여자가 아닙니다."
                                }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "대결방 없음",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                {
                                  "errorCode": "BATTLE_NOT_FOUND",
                                  "message": "대결을 찾을 수 없습니다."
                                }"""
                                    )
                            )
                    )
            }
    )
    @MessageMapping("/chat/message")
    void sendMessage(
            @Parameter(
                    description = "현재 사용자 ID (JWT에서 자동 추출)",
                    hidden = true
            )
            @CurrentUser String userId,

            @Parameter(
                    description = "메시지 전송 요청 (battleId, content)",
                    required = true,
                    schema = @Schema(implementation = SendMessageRequest.class)
            )
            SendMessageRequest request
    );
}
