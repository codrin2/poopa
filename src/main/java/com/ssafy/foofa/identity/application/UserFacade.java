package com.ssafy.foofa.identity.application;

import com.ssafy.foofa.identity.domain.User;
import com.ssafy.foofa.identity.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;

    public User getUser(String userId) {
        return userService.getUserIfExists(userId);
    }

    @Transactional
    public void deleteUser(String userId, Long withdrawalReasonId, String customReason) {
        userService.deleteUser(userId);
    }
}
