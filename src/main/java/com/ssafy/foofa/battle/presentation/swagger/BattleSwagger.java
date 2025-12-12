package com.ssafy.foofa.battle.presentation.swagger;

import com.ssafy.foofa.battle.domain.Battle;
import com.ssafy.foofa.battle.domain.BattleStatus;
import com.ssafy.foofa.battle.domain.dto.CreateBattleRequest;
import com.ssafy.foofa.core.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Battle API",
        description = """
        <b>대결 관련 API입니다.</b><br>
        오른쪽 상단 Authorize에 토큰 값을 넣어야합니다.
        """
)
@RequestMapping("/battles")
public interface BattleSwagger {

    /*──────────────────────────────────────────────────────
     * 1. 대결 생성
     *──────────────────────────────────────────────────────*/
    @Operation(
            summary = "대결 생성",
            description = """
            새로운 대결을 생성합니다.<br>
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "대결 생성 성공"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "필드 누락",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "message": "잘못된 JSON 형식입니다. 요청 데이터를 확인하세요."
                        }"""
                                    )
                            )
                    ),
            }
    )
    @PostMapping
    void createBattle(
            @Parameter(
                    description = "대결 생성 요청",
                    required = true,
                    schema = @Schema(implementation = CreateBattleRequest.class),
                    example = """
                    {
                      "inviteCode": "ABC123",
                      "setting": {
                        "duration": 7,
                        "mealTimes": ["BREAKFAST", "LUNCH", "DINNER"],
                        "maxCheatDays": 2
                      }
                    }"""
            )
            @RequestBody CreateBattleRequest createBattleRequest,
            @RequestAttribute("userId") String userId
    );

    /*──────────────────────────────────────────────────────
     * 2. 대결 삭제
     *──────────────────────────────────────────────────────*/
    @Operation(
            summary = "대결 삭제",
            description = """
            특정 대결을 삭제합니다.<br>
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "삭제 성공"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "대결을 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "message": "대결을 찾을 수 없습니다."
                        }"""
                                    )
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteBattle(
            @Parameter(description = "삭제할 대결 ID", required = true)
            @PathVariable String id
    );

    /*──────────────────────────────────────────────────────
     * 3. 상태별 대결 조회
     *──────────────────────────────────────────────────────*/
    @Operation(
            summary = "상태별 대결 조회",
            description = """
            특정 상태의 대결 목록을 조회합니다.<br>
            <b>상태 종류:</b><br>
            • <code>PENDING</code> : 대기 중<br>
            • <code>IN_PROGRESS</code> : 진행 중<br>
            • <code>COMPLETED</code> : 완료됨
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = Battle.class),
                                    examples = @ExampleObject(
                                            value = """
                        [
                          {
                            "id": "67567d5e8a9b123456789abc",
                            "inviteCode": "ABC123",
                            "status": "IN_PROGRESS",
                            "settings": {
                              "duration": 7,
                              "mealTimes": ["BREAKFAST", "LUNCH", "DINNER"],
                              "maxCheatDays": 2
                            },
                            "members": [
                              {
                                "userId": "user123",
                                "role": "HOST",
                                "score": 15,
                                "remainingCheatDays": 1,
                                "isWinner": false
                              },
                              {
                                "userId": "user456",
                                "role": "GUEST",
                                "score": 12,
                                "remainingCheatDays": 2,
                                "isWinner": false
                              }
                            ],
                            "winnerId": null,
                            "startDate": "2025-12-01T09:00:00",
                            "endDate": "2025-12-08T09:00:00"
                          }
                        ]"""
                                    )
                            )
                    ),
            }
    )
    @GetMapping
    ResponseEntity<List<Battle>> getBattlesByStatus(
            @Parameter(
                    description = "대결 상태",
                    required = true,
                    schema = @Schema(allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"})
            )
            @RequestParam BattleStatus status
    );

    /*──────────────────────────────────────────────────────
     * 4. 대결 결과 조회
     *──────────────────────────────────────────────────────*/
    @Operation(
            summary = "대결 결과 조회",
            description = """
            완료된 대결의 본인 결과를 조회합니다.<br>
            상태가 <code>COMPLETED</code>인 대결과 토큰의 userId로 본인의 결과만 조회됩니다.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = Battle.Member.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "userId": "user123",
                          "role": "HOST",
                          "score": 18,
                          "remainingCheatDays": 0,
                          "isWinner": true,
                          "joinedAt": "2025-12-01T09:00:00"
                        }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "대결이 완료되지 않음 / 대결의 멤버가 아님",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "BATTLE_NOT_COMPLETED",
                                                    summary = "대결 미완료",
                                                    value = """
                        {
                          "message": "배틀이 완료되지 않았습니다."
                        }"""
                                            ),
                                            @ExampleObject(
                                                    name = "MEMBER_NOT_FOUND",
                                                    summary = "멤버가 아님",
                                                    value = """
                        {
                          "message": "멤버를 찾을 수 없습니다."
                        }"""
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "대결을 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "message": "대결을 찾을 수 없습니다."
                        }"""
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{battleId}/result")
    ResponseEntity<Battle.Member> getBattleResult(
            @Parameter(description = "대결 ID", required = true)
            @PathVariable String battleId,
            @RequestAttribute("userId") String userId
    );
}