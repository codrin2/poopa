package com.ssafy.foofa.identity.domain;

import com.ssafy.foofa.identity.domain.dto.UserInfo;

public interface OauthApi {
    UserInfo fetchUser(String accessToken);
}
