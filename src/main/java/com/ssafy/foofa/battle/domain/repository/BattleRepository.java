package com.ssafy.foofa.battle.domain.repository;

import com.ssafy.foofa.battle.domain.Battle;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BattleRepository extends MongoRepository<Battle, String> {

}
