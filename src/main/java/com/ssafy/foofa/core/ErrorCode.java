package com.ssafy.foofa.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Validation
    INVALID_JSON("잘못된 JSON 형식입니다. 요청 데이터를 확인하세요."),
    INVALID_INPUT("입력이 잘못되었습니다."),
    MISSING_PARAMETER("필수 파라미터가 누락되었습니다."),
    INVALID_TYPE("입력한 값의 타입이 잘못되었습니다."),
    CLIENT_DISCONNECTED("이미 클라이언트에서 요청이 종료되었습니다."),
    RESOURCE_NOT_FOUND("요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_SUPPORTED("허용되지 않은 메서드입니다."),
    MEDIA_TYPE_NOT_SUPPORTED("허용되지 않은 미디어 타입입니다."),
    SERVER_ERROR("서버 오류가 발생했습니다. 관리자에게 문의해주세요."),

    // User
    USER_NOT_FOUND("회원을 찾을 수 없습니다. UserId: %s"),

    // Token
    TOKEN_EXPIRED("토큰이 만료되었습니다. 새로운 토큰을 재발급 받으세요."),
    TOKEN_INVALID("유효하지 않은 토큰입니다. 다시 로그인해 주세요."),
    REFRESH_TOKEN_EXPIRED("리프레시 토큰이 만료되었습니다. 다시 로그인해 주세요."),
    TOKEN_HEADER_INVALID("토큰 헤더 형식이 잘못되었습니다."),
    TOKEN_MISSING("토큰이 요청 헤더에 없습니다. 새로운 토큰을 재발급 받으세요."),
    TOKEN_BLACKLISTED("해당 토큰은 사용이 금지되었습니다. 다시 로그인해 주세요."),

    // OAuth
    UNSUPPORTED_OAUTH_PROVIDER("지원하지 않는 소셜 로그인 타입입니다. type: %s"),
    OAUTH_USER_FETCH_FAILED("%s 사용자 정보를 가져오는 데 실패했습니다. 잠시 후 다시 시도해주세요."),

    // Chat
    USER_NOT_IN_READBY_MAP("사용자가 readBy 맵에 존재하지 않습니다."),

    // Battle
    BATTLE_ALREADY_FULL("대결에 이미 2명의 멤버가 있습니다."),
    BATTLE_NEEDS_TWO_MEMBERS("대결을 시작하려면 정확히 2명의 멤버가 필요합니다."),
    NO_CHEAT_DAYS_REMAINING("남은 치팅데이가 없습니다."),
    OPPONENT_NOT_FOUND("상대방을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND("멤버를 찾을 수 없습니다."),
    NOT_FOUND_BATTLE("대결을 찾을 수 없습니다."),
    ;

    private final String message;

    public String getMessage() {
        return message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
