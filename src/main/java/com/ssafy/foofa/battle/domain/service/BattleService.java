package com.ssafy.foofa.battle.domain.service;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.BattleStatus;
import com.ssafy.foofa.battle.domain.repository.BattleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

}
