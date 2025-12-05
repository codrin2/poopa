package com.ssafy.foofa.battle.application;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.exception.NotFoundBattleException;
import com.ssafy.foofa.battle.domain.repository.BattleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BattleService {
    private final BattleRepository battleRepository;

    public void createBattle(String inviteCode, String hostUserId, Battle.Settings settings) {
        Battle newBattle = Battle.createNewBattle(inviteCode, hostUserId, settings);
        battleRepository.save(newBattle);
    }

    public void deleteBattle(Battle battle) {
        battleRepository.delete(battle);
    }

    public Battle findBattleById(String id) {
        return battleRepository.findById(id).orElseThrow(() -> new NotFoundBattleException(id));
    }
}
