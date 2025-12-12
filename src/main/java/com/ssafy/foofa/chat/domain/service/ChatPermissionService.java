package com.ssafy.foofa.chat.domain.service;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatPermissionService {

    public void validateSendPermission(Battle battle, String userId) {
        if (battle == null) {
            throw new IllegalArgumentException(ErrorCode.BATTLE_NOT_FOUND.getMessage());
        }

        if (!battle.isMember(userId)) {
            throw new IllegalArgumentException(ErrorCode.USER_NOT_BATTLE_MEMBER.getMessage());
        }

        if (!battle.isActive()) {
            throw new IllegalStateException(ErrorCode.BATTLE_NOT_ACTIVE.getMessage());
        }
    }
}
