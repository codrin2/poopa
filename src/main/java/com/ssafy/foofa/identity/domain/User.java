package com.ssafy.foofa.identity.domain;

import com.ssafy.foofa.identity.domain.enums.OauthProvider;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@CompoundIndex(name = "oauth_info_idx", def = "{'oauthInfo.provider': 1, 'oauthInfo.providerId': 1}", unique = true)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    private String id;

    private String email;

    private OauthInfo oauthInfo;

    private String nickname;

    private String profileImage;

    private Stats stats;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * OAuth 인증 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OauthInfo {
        private OauthProvider provider;
        private String providerId;
    }

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
     * OAuth로 새 사용자 등록
     */
    public static User registerWithOauth(String email, OauthProvider provider, String providerId) {
        return User.builder()
                .email(email)
                .oauthInfo(OauthInfo.builder()
                        .provider(provider)
                        .providerId(providerId)
                        .build())
                .stats(Stats.createDefault())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 프로필 업데이트
     */
    public User updateProfile(String nickname, String profileImage) {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .oauthInfo(this.oauthInfo)
                .nickname(nickname)
                .profileImage(profileImage)
                .stats(this.stats)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 승리 기록
     */
    public User recordWin() {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .oauthInfo(this.oauthInfo)
                .nickname(this.nickname)
                .profileImage(this.profileImage)
                .stats(this.stats.incrementWins())
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 패배 기록
     */
    public User recordLoss() {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .oauthInfo(this.oauthInfo)
                .nickname(this.nickname)
                .profileImage(this.profileImage)
                .stats(this.stats.incrementLosses())
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
