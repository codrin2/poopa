package com.ssafy.foofa.core;

import com.ssafy.foofa.identity.domain.enums.OauthProvider;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static java.util.Locale.ENGLISH;

@Component
public class OauthProviderConverter implements Converter<String, OauthProvider> {
    @Override
    public OauthProvider convert(String type) {
        try {
            return OauthProvider.valueOf(type.toUpperCase(ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER.format(type));
        }
    }
}
