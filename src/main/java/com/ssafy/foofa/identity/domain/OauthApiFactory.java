package com.ssafy.foofa.identity.domain;

import com.ssafy.foofa.identity.domain.enums.OauthProvider;

public interface OauthApiFactory {
    OauthApi getOauthApi(OauthProvider provider);
}
