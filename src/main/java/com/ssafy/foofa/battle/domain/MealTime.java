package com.ssafy.foofa.battle.domain;

import lombok.Getter;

@Getter
public enum MealTime {
    BREAKFAST("아침"),
    LUNCH("점심"),
    DINNER("저녁");

    private final String koreanName;

    MealTime(String koreanName) {
        this.koreanName = koreanName;
    }
}
