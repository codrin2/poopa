package com.ssafy.foofa.battle.presentation;

import com.ssafy.foofa.battle.application.BattleFacade;
import com.ssafy.foofa.battle.domain.BattleStatus;
import com.ssafy.foofa.battle.presentation.dto.BattleListResponse;
import com.ssafy.foofa.battle.presentation.dto.BattleResultResponse;
import com.ssafy.foofa.battle.presentation.dto.CreateBattleRequest;
import com.ssafy.foofa.battle.domain.service.BattleService;
import com.ssafy.foofa.battle.presentation.swagger.BattleSwagger;
import com.ssafy.foofa.core.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/battles")
@RequiredArgsConstructor
public class BattleController implements BattleSwagger{
    private final BattleService battleService;
    private final BattleFacade battleFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBattle(@RequestBody CreateBattleRequest createBattleRequest, @CurrentUser String userId) {
        battleService.createBattle(createBattleRequest.inviteCode(), userId, createBattleRequest.setting());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBattle(@PathVariable String id) {
        battleService.deleteByBattleId(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<BattleListResponse>> getBattlesByStatus(@RequestParam BattleStatus status, @CurrentUser String userId) {
        return ResponseEntity.ok(battleFacade.getBattleList(status, userId));
    }

    @GetMapping("/{battleId}/result")
    public ResponseEntity<BattleResultResponse> getBattleResult(@PathVariable String battleId, @CurrentUser String userId) {
        return ResponseEntity.ok(battleFacade.getBattleResult(battleId, userId));
    }
}
