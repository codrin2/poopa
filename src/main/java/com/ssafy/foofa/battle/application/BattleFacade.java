package com.ssafy.foofa.battle.application;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.BattleStatus;
import com.ssafy.foofa.battle.domain.service.BattleService;
import com.ssafy.foofa.battle.presentation.dto.BattleListResponse;
import com.ssafy.foofa.battle.presentation.dto.BattleResultResponse;
import com.ssafy.foofa.core.ErrorCode;
import com.ssafy.foofa.identity.domain.User;
import com.ssafy.foofa.identity.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BattleFacade {
    private final BattleService battleService;
    private final UserService userService;

    public BattleResultResponse getBattleResult(String battleId, String userId) {
        Battle battle = battleService.validateAndGetBattle(battleId);
        if (battle.getStatus() != BattleStatus.COMPLETED) {
            throw new IllegalArgumentException(ErrorCode.BATTLE_NOT_COMPLETED.getMessage());
        }
        Battle.Member user = battle.getMember(userId);
        Battle.Member opponentUser = battle.getMember(battle.getOpponentUserId(userId));
        User userInfo = userService.getUserIfExists(user.getUserId());
        return BattleResultResponse.create(battle.getId(), user.getScore(), userInfo.getNickname(), opponentUser.getScore(), user.getIsWinner());
    }

    public List<BattleListResponse> getBattleList(BattleStatus status, String userId) {
        List<Battle> battles = battleService.getBattleStatus(status, userId);
        return battles.stream()
                .map(battle -> {
                    Battle.Member user = battle.getMember(userId);
                    String opponentName = null;
                    Long remainingDays = null;
                    Integer opponentScore = null;
                    if (battle.getStatus() != BattleStatus.PENDING) {
                        Battle.Member opponentUser = battle.getMember(battle.getOpponentUserId(userId));
                        opponentScore = opponentUser.getScore();
                        opponentName = userService.getUserIfExists(user.getUserId()).getNickname();
                        remainingDays = ChronoUnit.DAYS.between(battle.getEndDate(), battle.getStartDate());
                    }
                    return BattleListResponse.create(battle.getId(), user.getScore(), battle.getSettings().getMealTimes(), remainingDays, opponentName, opponentScore);
                }).toList();
    }

//    @Scheduled(cron = "* * * * * *") //테스트 용
    /*
     * 9시 : 아침 식사 인증 마지막 시간
     * 14시 : 점심 식사 인증 마지막 시간
     * 20시 : 저녁 식사 인증 마지막 시간
     * 식사 인증이 가능한 시간이 정해져 있기 때문에 인증 완료 시간에 맞춰서 대결을 완료 처리
     */
    @Scheduled(cron = "0 0 9,14,20 * * *")
    public void battleCompleted(){
        battleService.completeExpiredBattles();
    }
}
