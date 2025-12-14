package com.ssafy.foofa.chat.presentation.dto;

public record SendMessageRequest(
        String battleId,
        String content
) {
}
