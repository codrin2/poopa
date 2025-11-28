package com.ssafy.foofa.identity.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String kakaoId;

    private String nickname;

    private String profileImage;

    private String refreshToken;

    private Stats stats;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 사용자 통계 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Stats {
        private Integer totalBattles;
        private Integer wins;
        private Integer losses;
        private Integer currentStreak;
        private Integer bestStreak;

        public static Stats createDefault() {
            return Stats.builder()
                    .totalBattles(0)
                    .wins(0)
                    .losses(0)
                    .currentStreak(0)
                    .bestStreak(0)
                    .build();
        }

        // 통계 업데이트를 위한 메서드 (불변성 유지)
        public Stats incrementWins() {
            return Stats.builder()
                    .totalBattles(this.totalBattles + 1)
                    .wins(this.wins + 1)
                    .losses(this.losses)
                    .currentStreak(this.currentStreak + 1)
                    .bestStreak(Math.max(this.currentStreak + 1, this.bestStreak))
                    .build();
        }

        public Stats incrementLosses() {
            return Stats.builder()
                    .totalBattles(this.totalBattles + 1)
                    .wins(this.wins)
                    .losses(this.losses + 1)
                    .currentStreak(0)
                    .bestStreak(this.bestStreak)
                    .build();
        }
    }

    /**
     * 새 사용자 생성
     */
    public static User createNewUser(String kakaoId, String nickname, String profileImage) {
        return User.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .profileImage(profileImage)
                .stats(Stats.createDefault())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 리프레시 토큰 업데이트 (새 인스턴스 반환)
     */
    public User updateRefreshToken(String refreshToken) {
        return User.builder()
                .id(this.id)
                .kakaoId(this.kakaoId)
                .nickname(this.nickname)
                .profileImage(this.profileImage)
                .refreshToken(refreshToken)
                .stats(this.stats)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 프로필 업데이트 (새 인스턴스 반환)
     */
    public User updateProfile(String nickname, String profileImage) {
        return User.builder()
                .id(this.id)
                .kakaoId(this.kakaoId)
                .nickname(nickname)
                .profileImage(profileImage)
                .refreshToken(this.refreshToken)
                .stats(this.stats)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 승리 기록 (새 인스턴스 반환)
     */
    public User recordWin() {
        return User.builder()
                .id(this.id)
                .kakaoId(this.kakaoId)
                .nickname(this.nickname)
                .profileImage(this.profileImage)
                .refreshToken(this.refreshToken)
                .stats(this.stats.incrementWins())
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 패배 기록 (새 인스턴스 반환)
     */
    public User recordLoss() {
        return User.builder()
                .id(this.id)
                .kakaoId(this.kakaoId)
                .nickname(this.nickname)
                .profileImage(this.profileImage)
                .refreshToken(this.refreshToken)
                .stats(this.stats.incrementLosses())
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
