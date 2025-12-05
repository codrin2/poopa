package com.ssafy.foofa.identity.domain.service;

import com.ssafy.foofa.core.ErrorCode;
import com.ssafy.foofa.identity.domain.User;
import com.ssafy.foofa.identity.domain.dto.UserInfo;
import com.ssafy.foofa.identity.domain.enums.OauthProvider;
import com.ssafy.foofa.identity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public User getUserIfExists(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.format(userId)));
    }

    @Transactional
    public User findOrRegisterOauthUser(UserInfo userInfo, OauthProvider oauthProvider) {
        return userRepository.findByOauthInfoProviderIdAndOauthInfoProvider(
                        userInfo.oauthProviderId(),
                        oauthProvider
                )
                .orElseGet(() -> registerUserWithOauth(userInfo, oauthProvider));
    }

    private User registerUserWithOauth(UserInfo userInfo, OauthProvider oauthProvider) {
        User newUser = User.registerWithOauth(
                userInfo.email(),
                oauthProvider,
                userInfo.oauthProviderId()
        );
        return userRepository.save(newUser);
    }

    @Transactional
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
