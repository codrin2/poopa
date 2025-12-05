package com.ssafy.foofa.identity.application;

import com.ssafy.foofa.identity.domain.OauthApi;
import com.ssafy.foofa.identity.domain.OauthApiFactory;
import com.ssafy.foofa.identity.domain.User;
import com.ssafy.foofa.identity.domain.dto.UserInfo;
import com.ssafy.foofa.identity.domain.enums.OauthProvider;
import com.ssafy.foofa.identity.domain.service.UserService;
import com.ssafy.foofa.identity.presentation.dto.LoginResponse;
import com.ssafy.foofa.identity.presentation.dto.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthFacade {
    private final TokenFacade tokenFacade;
    private final OauthApiFactory oauthApiFactory;
    private final UserService userService;

    @Transactional
    public LoginResponse loginWithOAuth(OauthProvider oauthProvider, String accessToken) {
        OauthApi oauthApi = oauthApiFactory.getOauthApi(oauthProvider);
        UserInfo userInfo = oauthApi.fetchUser(accessToken);

        User user = userService.findOrRegisterOauthUser(userInfo, oauthProvider);
        Token token = tokenFacade.issue(user.getId());

        return LoginResponse.of(user.getId(), token.accessToken(), token.refreshToken());
    }
}
