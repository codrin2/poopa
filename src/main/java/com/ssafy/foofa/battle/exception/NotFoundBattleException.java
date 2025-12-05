package com.ssafy.foofa.battle.exception;

import com.ssafy.foofa.core.exception.NotFoundException;

import static com.ssafy.foofa.core.ErrorCode.NOT_FOUND_BATTLE;

public class NotFoundBattleException extends NotFoundException {
    public NotFoundBattleException(String id) {
        super(NOT_FOUND_BATTLE.getMessage().formatted(id));
    }

    @Override
    public String getErrorCode() {
        return NOT_FOUND_BATTLE.name();
    }
}
