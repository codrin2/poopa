package com.ssafy.foofa.battle.application;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.BattleStatus;
import com.ssafy.foofa.battle.domain.service.BattleService;
import com.ssafy.foofa.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BattleFacade {
    private final BattleService battleService;

    public Battle.Member getBattleResult(String battleId, String userId) {
        Battle battle = battleService.getBattleById(battleId);
        if (battle.getStatus() == BattleStatus.COMPLETED) {
            throw new IllegalArgumentException(ErrorCode.BATTLE_NOT_COMPLETED.getMessage());
        }
        List<Battle.Member> members = battle.getMembers();
        Battle.Member member = members.stream().filter(m -> m.getUserId().equals(userId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(ErrorCode.MEMBER_NOT_FOUND.format(userId)));
        return member;
    }
}
