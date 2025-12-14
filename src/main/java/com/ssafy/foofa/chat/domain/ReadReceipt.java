package com.ssafy.foofa.chat.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadReceipt {
    private String userId;
    private LocalDateTime readAt;

    /**
     * 읽음 상태로 생성
     */
    public static ReadReceipt createRead(String userId) {
        return ReadReceipt.builder()
                .userId(userId)
                .readAt(LocalDateTime.now())
                .build();
    }

    /**
     * 안읽음 상태로 생성
     */
    public static ReadReceipt createUnread(String userId) {
        return ReadReceipt.builder()
                .userId(userId)
                .readAt(null)
                .build();
    }

    /**
     * 읽음 여부 확인
     */
    public boolean isRead() {
        return readAt != null;
    }
}
