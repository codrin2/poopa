package com.ssafy.foofa.battle.domain.dto;

import com.ssafy.foofa.battle.domain.Battle;

public record CreateBattleRequest(
        String inviteCode,
        String hostUserId,
        Battle.Settings setting
) {
}
