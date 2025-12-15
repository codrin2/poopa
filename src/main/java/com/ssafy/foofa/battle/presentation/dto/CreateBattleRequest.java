package com.ssafy.foofa.battle.presentation.dto;

import com.ssafy.foofa.battle.domain.Battle;

public record CreateBattleRequest(
        String inviteCode,
        Battle.Settings setting
) {
}
