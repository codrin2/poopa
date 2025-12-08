package com.ssafy.foofa.battle.presentation;

import com.ssafy.foofa.battle.domain.dto.CreateBattleRequest;
import com.ssafy.foofa.battle.domain.service.BattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/battles")
@RequiredArgsConstructor
public class BattleController {
    private final BattleService battleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBattle(@RequestBody CreateBattleRequest createBattleRequest) {
        battleService.createBattle(createBattleRequest.getInviteCode(), createBattleRequest.getHostUserId(), createBattleRequest.getSetting());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBattle(@PathVariable String id) {
        battleService.deleteByBattleId(id);
        return ResponseEntity.ok().build();
    }
}
