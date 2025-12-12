package com.ssafy.foofa.battle.presentation;

import com.ssafy.foofa.battle.application.BattleFacade;
import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.BattleStatus;
import com.ssafy.foofa.battle.domain.dto.CreateBattleRequest;
import com.ssafy.foofa.battle.domain.service.BattleService;
import com.ssafy.foofa.battle.presentation.swagger.BattleSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/battles")
@RequiredArgsConstructor
public class BattleController implements BattleSwagger {
    private final BattleService battleService;
    private final BattleFacade battleFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBattle(@RequestBody CreateBattleRequest createBattleRequest,  @RequestAttribute("userId") String userId) {
        battleService.createBattle(createBattleRequest.getInviteCode(), userId, createBattleRequest.getSetting());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBattle(@PathVariable String id) {
        battleService.deleteByBattleId(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Battle>> getBattlesByStatus(@RequestParam BattleStatus status) {
        List<Battle> battles = battleService.getBattleStatusBattles(status);
        return ResponseEntity.ok(battles);
    }

    @GetMapping("/{battleId}/result")
    public ResponseEntity<Battle.Member> getBattleResult(@PathVariable String battleId,  @RequestAttribute("userId") String userId) {
        Battle.Member member = battleFacade.getBattleResult(battleId, userId);
        return ResponseEntity.ok(member);
    }
}
