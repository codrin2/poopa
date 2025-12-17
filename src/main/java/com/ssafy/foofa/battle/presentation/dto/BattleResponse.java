package com.ssafy.foofa.battle.presentation.dto;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.BattleStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public record BattleResponse(
        String battleId,
        Integer myScore,
        List<String> mealTimes,
        Integer remainingHours,
        LocalDateTime endDate,
        String opponentName,
        Integer opponentScore
) {
    public static BattleResponse from(Battle battle, String userId, Map<String, String> nicknameMap) {
        Battle.Member user = battle.getMember(userId);
        String opponentName = null;
        Integer remainingHours = null;
        Integer opponentScore = null;
        if (battle.getStatus() != BattleStatus.PENDING) {
            Battle.Member opponentUser = battle.getMember(battle.getOpponentUserId(userId));
            opponentScore = opponentUser.getScore();
            opponentName = nicknameMap.get(opponentUser.getUserId());
            remainingHours = (int) ChronoUnit.HOURS.between(LocalDateTime.now(), battle.getEndDate());
        }
        return new BattleResponse(
                battle.getId(),
                user.getScore(),
                battle.getSettings().getMealTimes(),
                remainingHours,
                battle.getEndDate(),
                opponentName,
                opponentScore);
    }
}
