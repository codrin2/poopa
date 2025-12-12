package com.ssafy.foofa.battle.domain.repository;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.BattleStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BattleRepository extends MongoRepository<Battle, String> {
    List<Battle> findBattleByStatus(BattleStatus status);
    Battle findBattleById(String id);
    List<Battle> findBattleByStatusAndEndDateBefore(BattleStatus status, LocalDateTime now);

}
