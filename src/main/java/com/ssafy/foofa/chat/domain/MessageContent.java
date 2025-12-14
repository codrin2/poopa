package com.ssafy.foofa.chat.domain;

import com.ssafy.foofa.core.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageContent {
    private static final int MAX_LENGTH = 1000;

    private String value;

    public static MessageContent of(String content) {
        validate(content);
        return new MessageContent(content);
    }

    private static void validate(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(ErrorCode.MESSAGE_CONTENT_EMPTY.getMessage());
        }

        if (content.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                ErrorCode.MESSAGE_CONTENT_TOO_LONG.format(MAX_LENGTH)
            );
        }

        // XSS 방지를 위한 HTML 태그 검증
        if (containsHtmlTags(content)) {
            throw new IllegalArgumentException(ErrorCode.MESSAGE_CONTENT_HTML_NOT_ALLOWED.getMessage());
        }
    }

    private static boolean containsHtmlTags(String content) {
        return content.matches(".*<[^>]+>.*");
    }
}
