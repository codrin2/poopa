package com.ssafy.foofa.battle.domain.dto;

import com.ssafy.foofa.battle.domain.Battle;
import lombok.Getter;

@Getter
public class CreateBattleRequest {
    private String inviteCode;
    private String hostUserId;
    private Battle.Settings setting;
}
