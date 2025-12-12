package com.ssafy.foofa.battle.domain.service;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.BattleStatus;
import com.ssafy.foofa.battle.domain.MemberRole;
import com.ssafy.foofa.battle.domain.repository.BattleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BattleService {
    private final BattleRepository battleRepository;

    public void createBattle(String inviteCode, String hostUserId, Battle.Settings settings) {
        Battle newBattle = Battle.createNewBattle(inviteCode, hostUserId, settings);
        battleRepository.save(newBattle);
    }

    public void deleteByBattleId(String id) {
        battleRepository.deleteById(id);
    }

    public List<Battle> getBattleStatusBattles(BattleStatus status) {
        return battleRepository.findBattleByStatus(status);
    }

    public Battle getBattleById(String id) {
        return battleRepository.findBattleById(id);
    }

    public void completeExpiredBattles() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        switch (now.getHour()) {
            case 9 ->  now = now.plusHours(3);
            case 14 -> now = now.plusHours(4);
            case 20 -> now = now.plusHours(11);
        }
        List<Battle> expiredBattles = battleRepository.findBattleByStatusAndEndDateBefore(BattleStatus.IN_PROGRESS, now);
        expiredBattles.forEach(battle -> {
            Battle.Member host = battle.getMemberByRole(MemberRole.HOST);
            Battle.Member guest = battle.getMemberByRole(MemberRole.GUEST);
            String winnerId = host.getScore()>guest.getScore()?host.getUserId():guest.getUserId();
            Battle completedBattle = battle.complete(winnerId);
            battleRepository.save(completedBattle);
        });
    }

}
