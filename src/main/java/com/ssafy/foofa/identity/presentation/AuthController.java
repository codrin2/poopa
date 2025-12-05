package com.ssafy.foofa.identity.presentation;

import com.ssafy.foofa.identity.application.AuthFacade;
import com.ssafy.foofa.identity.application.TokenFacade;
import com.ssafy.foofa.identity.domain.enums.OauthProvider;
import com.ssafy.foofa.identity.presentation.dto.AccessToken;
import com.ssafy.foofa.identity.presentation.dto.LoginResponse;
import com.ssafy.foofa.identity.presentation.dto.Token;
import com.ssafy.foofa.identity.presentation.swagger.AuthSwagger;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthSwagger {
    private final AuthFacade authFacade;
    private final TokenFacade tokenFacade;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login/oauth")
    public LoginResponse loginWithOAuth(
            @RequestParam("provider") OauthProvider oauthProvider,
            @RequestParam("access_token") String accessToken
    ) {
        return authFacade.loginWithOAuth(oauthProvider, accessToken);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reissue")
    public Token reissue(
            @CookieValue("refreshToken") String refreshToken
    ) {
        return tokenFacade.reissue(refreshToken);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(
            HttpServletResponse response
    ) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/test/token")
    public AccessToken testToken() {
        Token token = tokenFacade.issue("test-user-id");
        return new AccessToken(token.accessToken());
    }
}
