package com.ssafy.foofa.identity.presentation.swagger;

import com.ssafy.foofa.core.ErrorResponse;
import com.ssafy.foofa.identity.domain.enums.OauthProvider;
import com.ssafy.foofa.identity.presentation.dto.AccessToken;
import com.ssafy.foofa.identity.presentation.dto.LoginResponse;
import com.ssafy.foofa.identity.presentation.dto.Token;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Auth API",
        description = """
        <b>인증 관련 API입니다.</b><br><br>
        <b>로그인 후 이용하는 API에서 발생 가능한 토큰 오류</b><br>
        • <code>TOKEN_MISSING</code> : Authorization 헤더 없음<br>
        • <code>INVALID_TOKEN_HEADER</code>: "Bearer " 접두사 누락<br>
        • <code>TOKEN_EXPIRED</code> : Access Token 만료<br>
        • <code>TOKEN_INVALID</code> : 위·변조 또는 형식 오류<br><br>
        <b>🔑 OauthProvider ENUM</b> : <code>KAKAO</code> | <code>APPLE</code>
        """
)
@RequestMapping("/auth")
public interface AuthSwagger {

    /*──────────────────────────────────────────────────────
     * 1. OAuth 로그인
     *──────────────────────────────────────────────────────*/
    @Operation(
            summary = "OAuth 로그인",
            description = """
            소셜 Access Token으로 로그인하고 자체 JWT Access/Refresh Token을 발급합니다.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공",
                            content = @Content(
                                    schema = @Schema(implementation = LoginResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "memberId": 99,
                          "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "isNewMember": false
                        }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "지원하지 않는 provider / 파라미터 누락",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "UNSUPPORTED_SOCIAL_LOGIN",
                          "message": "지원하지 않는 소셜 로그인 타입입니다. type : NAVER"
                        }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "소셜 토큰 만료·위조",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "TOKEN_INVALID",
                          "message": "유효하지 않은 토큰입니다. 다시 로그인해 주세요."
                        }"""
                                    )
                            )
                    )
            }
    )
    @PostMapping("/login/oauth")
    LoginResponse loginWithOAuth(
            @Parameter(description = "OAuth 제공자", example = "KAKAO", required = true)
            @RequestParam("provider") OauthProvider provider,
            @Parameter(description = "소셜 Access Token", required = true)
            @RequestParam("access_token") String accessToken
    );

    /*──────────────────────────────────────────────────────
     * 2. 토큰 재발급
     *──────────────────────────────────────────────────────*/
    @Operation(
            summary = "JWT 재발급",
            description = """
            쿠키에 저장된 Refresh Token으로 새 Access/Refresh Token을 발급합니다.<br>
            """,
            responses = {
                    /*──── 200 : 성공 ─────────────────────────────*/
                    @ApiResponse(
                            responseCode = "200",
                            description = "재발급 성공",
                            content = @Content(schema = @Schema(implementation = Token.class))
                    ),

                    /*──── 401 : 인증 오류 모음 ───────────────────*/
                    @ApiResponse(
                            responseCode = "400",
                            description = """
                            인증/토큰 관련 오류<br>
                            • <code>MISSING_PARAMETER</code> - refreshToken 쿠키 누락<br>
                            • <code>TOKEN_INVALID</code> - 토큰 위·변조 또는 형식 오류<br>
                            • <code>REFRESH_TOKEN_EXPIRED</code> - 토큰 만료<br>
                            """,
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "MISSING_PARAMETER",
                                                    summary = "refreshToken 쿠키 누락",
                                                    value = """
                        {
                          "message": "필수 파라미터가 누락되었습니다."
                        }"""
                                            ),
                                            @ExampleObject(
                                                    name = "TOKEN_INVALID",
                                                    summary = "토큰 위·변조 / 형식 오류",
                                                    value = """
                        {
                          "message": "유효하지 않은 토큰입니다. 다시 로그인해 주세요."
                        }"""
                                            ),
                                            @ExampleObject(
                                                    name = "REFRESH_TOKEN_EXPIRED",
                                                    summary = "Refresh Token 만료",
                                                    value = """
                        {
                          "message": "리프레시 토큰이 만료되었습니다. 다시 로그인해 주세요."
                        }"""
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping("/reissue")
    Token reissue(
            @Parameter(
                    description = "Refresh Token (쿠키에서 자동으로 가져옴)",
                    example     = "eyJhbGciOiJIUzI1NiJ9...",
                    required    = true
            )
            @CookieValue("refreshToken") String refreshToken
    );

    /*──────────────────────────────────────────────────────
     * 3. 로그아웃
     *──────────────────────────────────────────────────────*/
    @Operation(
            summary = "로그아웃",
            description = """
            Refresh Token을 쿠키에서 제거합니다.<br>
            """,
            responses = {
                    @ApiResponse(responseCode = "204", description = "로그아웃 완료")
            }
    )
    @PostMapping("/logout")
    void logout(
            HttpServletResponse response
    );

    /*──────────────────────────────────────────────────────
     * 4. (개발용) Access Token 발급
     *──────────────────────────────────────────────────────*/
    @Operation(
            summary = "테스트용 Access Token 발급",
            description = "테스트 용으로 memberId = 1 사용자를 가정해 Access Token을 발급합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "발급 성공",
                            content = @Content(
                                    schema = @Schema(implementation = AccessToken.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                        }"""
                                    )
                            )
                    )
            }
    )
    @PostMapping("/test/token")
    AccessToken testToken();
}
