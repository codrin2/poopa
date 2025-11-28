package com.ssafy.foofa.battle.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "cheat_day_logs")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CheatDayLog {

    @Id
    private String id;

    private String battleId;

    private String userId;

    private LocalDate targetDate;

    private MealTime mealTime;

    private LocalDateTime createdAt;

    /**
     * 새로운 치팅데이 로그 생성
     */
    public static CheatDayLog create(String battleId, String userId,
                                     LocalDate targetDate, MealTime mealTime) {
        return CheatDayLog.builder()
                .battleId(battleId)
                .userId(userId)
                .targetDate(targetDate)
                .mealTime(mealTime)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 오늘 사용한 치팅데이인지 확인
     */
    public boolean isToday() {
        return this.targetDate.equals(LocalDate.now());
    }
}
