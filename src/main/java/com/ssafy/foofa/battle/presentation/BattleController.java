package com.ssafy.foofa.battle.presentation;

import com.ssafy.foofa.battle.application.BattleService;
import com.ssafy.foofa.battle.domain.Battle;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/battles")
@RequiredArgsConstructor
public class BattleController {
    private final BattleService battleService;

    @PostMapping
    public ResponseEntity<String> createBattle(@RequestParam String inviteCode, @RequestParam String hostUserId, @RequestBody Battle.Settings settings) {
        battleService.createBattle(inviteCode, hostUserId, settings);
        return ResponseEntity.ok().body("대결을 생성했습니다.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBattle(@PathVariable String id) {
        Battle battle = battleService.findBattleById(id);
        battleService.deleteBattle(battle);
        return ResponseEntity.ok().body("대결을 삭제했습니다.");
    }
}
