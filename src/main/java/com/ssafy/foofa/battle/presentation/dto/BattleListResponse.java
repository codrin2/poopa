package com.ssafy.foofa.battle.presentation.dto;

import java.util.List;

public record BattleListResponse (
        String battleId,
        Integer myScore,
        List<String> mealTimes,
        Long remainingDays,
        String opponentName,
        Integer opponentScore
) {
    public static BattleListResponse create(String battleId, Integer myScore, List<String> mealTimes, Long remainingDays, String opponentName, Integer opponentScore) {
        return new BattleListResponse(battleId, myScore,  mealTimes, remainingDays, opponentName, opponentScore);
    }
}
