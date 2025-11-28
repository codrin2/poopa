package com.ssafy.foofa.battle.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "meal_checkins")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MealCheckIn {

    @Id
    private String id;

    private String battleId;

    private String userId;

    private MealTime mealTime;

    private String imageUrl;

    private String menuName;

    private Scores scores;

    private String aiComment;

    private LocalDateTime createdAt;

    /**
     * 점수 상세 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Scores {
        private Integer total;
        private Integer nutrition;
        private Integer additives;
        private Integer freshness;
        private Integer time;

        /**
         * 총점 계산
         */
        public static Scores calculate(int nutrition, int additives, int freshness, int time) {
            int total = nutrition + additives + freshness + time;

            return Scores.builder()
                    .total(total)
                    .nutrition(nutrition)
                    .additives(additives)
                    .freshness(freshness)
                    .time(time)
                    .build();
        }
    }

    /**
     * 새로운 식사 인증 생성
     */
    public static MealCheckIn create(String battleId, String userId, MealTime mealTime,
                                     String imageUrl, String menuName, Scores scores,
                                     String aiComment) {
        return MealCheckIn.builder()
                .battleId(battleId)
                .userId(userId)
                .mealTime(mealTime)
                .imageUrl(imageUrl)
                .menuName(menuName)
                .scores(scores)
                .aiComment(aiComment)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 총점 조회
     */
    public int getTotalScore() {
        return scores != null ? scores.getTotal() : 0;
    }
}
