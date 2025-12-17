package com.ssafy.foofa.battle.application;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.BattleStatus;
import com.ssafy.foofa.battle.domain.service.BattleService;
import com.ssafy.foofa.battle.presentation.dto.BattleResponse;
import com.ssafy.foofa.battle.presentation.dto.BattleResultResponse;
import com.ssafy.foofa.core.ErrorCode;
import com.ssafy.foofa.identity.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BattleFacade {
    private final BattleService battleService;
    private final UserService userService;

    public BattleResultResponse getBattleResult(String battleId, String userId) {
        Battle battle = battleService.validateAndGetBattle(battleId);
        if (battle.isInProgress() && battle.isExpired()) {
            battleService.completeExpiredBattle(battleId);
            battle = battleService.validateAndGetBattle(battleId);
        }
        if (!battle.isCompleted()){
            throw new IllegalArgumentException(ErrorCode.BATTLE_NOT_COMPLETED.getMessage());
        }
        Battle.Member user = battle.getMember(userId);
        Battle.Member opponentUser = battle.getMember(battle.getOpponentUserId(userId));
        String opponentUserName = userService.getUserIfExists(opponentUser.getUserId()).getNickname();
        return BattleResultResponse.create(battle.getId(), user.getScore(), opponentUserName, opponentUser.getScore(), user.getIsWinner());
    }

    public List<BattleResponse> getBattleList(BattleStatus status, String userId) {
        List<Battle> battles = battleService.getBattleStatus(status, userId);
        Set<String> opponentUserIds = battles.stream().
                map(battle -> battle.getOpponentUserIdOrNull(userId))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<String, String> nicknameMap = userService.getNicknamesByUserIds(opponentUserIds);
        return battles.stream()
                .map(battle -> BattleResponse.from(battle, userId, nicknameMap))
                .toList();
    }

}
