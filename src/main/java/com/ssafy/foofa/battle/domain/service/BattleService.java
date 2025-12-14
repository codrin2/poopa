package com.ssafy.foofa.battle.domain.service;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.repository.BattleRepository;
import com.ssafy.foofa.core.ErrorCode;
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

    public void deleteByBattleId(String id) {
        battleRepository.deleteById(id);
    }

    public Battle validateAndGetBattle(String battleId) {
        return battleRepository.findById(battleId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.BATTLE_NOT_FOUND.getMessage()));
    }
}
