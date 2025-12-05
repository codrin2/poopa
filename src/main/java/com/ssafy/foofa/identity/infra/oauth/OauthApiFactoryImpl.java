package com.ssafy.foofa.identity.infra.oauth;

import com.ssafy.foofa.core.ErrorCode;
import com.ssafy.foofa.identity.domain.OauthApi;
import com.ssafy.foofa.identity.domain.OauthApiFactory;
import com.ssafy.foofa.identity.domain.enums.OauthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OauthApiFactoryImpl implements OauthApiFactory {
    private final Map<OauthProvider, OauthApi> oauthApiMap;

    @Autowired
    public OauthApiFactoryImpl(List<OauthApi> oauthApis) {
        oauthApiMap = Map.of(
                OauthProvider.KAKAO, oauthApis.stream().filter(api -> api instanceof KakaoOauthApi).findFirst().orElseThrow()
        );
    }

    @Override
    public OauthApi getOauthApi(OauthProvider provider) {
        return Optional.ofNullable(oauthApiMap.get(provider))
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER.format(provider.name())));
    }
}
