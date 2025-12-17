package com.ssafy.foofa.battle.presentation.dto;

public record BattleResultResponse (
        String battleId,
        Integer myScore,
        String opponentName,
        Integer opponentScore,
        boolean isWinner
) {
    public static BattleResultResponse create(
            String battleId,
            Integer myScore,
            String opponentName,
            Integer opponentScore,
            boolean isWinner) {
        return new BattleResultResponse(battleId, myScore, opponentName, opponentScore, isWinner);
    }
}
