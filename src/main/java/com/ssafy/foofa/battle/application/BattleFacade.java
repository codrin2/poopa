package com.ssafy.foofa.battle.application;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.BattleStatus;
import com.ssafy.foofa.battle.domain.service.BattleService;
import com.ssafy.foofa.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BattleFacade {
    private final BattleService battleService;

    public Battle.Member getBattleResult(String battleId, String userId) {
        Battle battle = battleService.getBattleById(battleId);
        if (battle.getStatus() != BattleStatus.COMPLETED) {
            throw new IllegalArgumentException(ErrorCode.BATTLE_NOT_COMPLETED.getMessage());
        }
        return battle.getMember(userId);
    }


//    @Scheduled(cron = "* * * * * *") //테스트 용
    @Scheduled(cron = "0 0 9,14,20 * * *")
    public void battleCompleted(){
        battleService.completeExpiredBattles();
    }
}
