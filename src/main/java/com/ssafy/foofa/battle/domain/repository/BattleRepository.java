package com.ssafy.foofa.battle.domain.repository;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.BattleStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BattleRepository extends MongoRepository<Battle, String> {
    List<Battle> findByStatusAndMembers_UserId(BattleStatus status, String userId);
    List<Battle> findByStatusAndEndDateBefore(BattleStatus status, LocalDateTime now);

}
