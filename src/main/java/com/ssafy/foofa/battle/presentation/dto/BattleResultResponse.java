package com.ssafy.foofa.battle.presentation.dto;

public record BattleResultResponse (
        String battleId,
        Integer myScore,
        String opponentName,
        Integer oppenentScore,
        boolean isWinner
) {
    public static BattleResultResponse create(String battleId, Integer myScore, String opponentName, Integer oppenentScore, boolean isWinner) {
        return new BattleResultResponse(battleId, myScore, opponentName, oppenentScore, isWinner);
    }
}
