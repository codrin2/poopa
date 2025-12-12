package com.ssafy.foofa.battle.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "battles")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Battle {

    @Id
    private String id;

    @Indexed(unique = true)
    private String inviteCode;

    private BattleStatus status;

    private Settings settings;

    @Builder.Default
    private List<Member> members = new ArrayList<>();

    @Builder.Default
    private Map<String, Integer> unreadCount = new HashMap<>();

    private LastMessage lastMessage;

    private String winnerId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 대결 설정
     */
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Settings {
        private Integer duration;
        private List<String> mealTimes;
        private Integer maxCheatDays;
    }

    /**
     * 대결 참여자
     */
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Member {
        private String userId;
        private MemberRole role;
        @Builder.Default
        private Integer score = 0;
        private Integer remainingCheatDays;
        @Builder.Default
        private Boolean isWinner = false;
        private LocalDateTime joinedAt;

        public static Member createHost(String userId, Integer maxCheatDays) {
            return Member.builder()
                    .userId(userId)
                    .role(MemberRole.HOST)
                    .score(0)
                    .remainingCheatDays(maxCheatDays)
                    .isWinner(false)
                    .joinedAt(LocalDateTime.now())
                    .build();
        }

        public static Member createGuest(String userId, Integer maxCheatDays) {
            return Member.builder()
                    .userId(userId)
                    .role(MemberRole.GUEST)
                    .score(0)
                    .remainingCheatDays(maxCheatDays)
                    .isWinner(false)
                    .joinedAt(LocalDateTime.now())
                    .build();
        }

        /**
         * 점수 추가 (새 인스턴스 반환)
         */
        public Member addScore(int points) {
            return Member.builder()
                    .userId(this.userId)
                    .role(this.role)
                    .score(this.score + points)
                    .remainingCheatDays(this.remainingCheatDays)
                    .isWinner(this.isWinner)
                    .joinedAt(this.joinedAt)
                    .build();
        }

        /**
         * 치팅데이 사용 (새 인스턴스 반환)
         */
        public Member useCheatDay() {
            if (this.remainingCheatDays <= 0) {
                throw new IllegalStateException("No cheat days remaining");
            }

            return Member.builder()
                    .userId(this.userId)
                    .role(this.role)
                    .score(this.score)
                    .remainingCheatDays(this.remainingCheatDays - 1)
                    .isWinner(this.isWinner)
                    .joinedAt(this.joinedAt)
                    .build();
        }

        /**
         * 승자 설정 (새 인스턴스 반환)
         */
        public Member markAsWinner() {
            return Member.builder()
                    .userId(this.userId)
                    .role(this.role)
                    .score(this.score)
                    .remainingCheatDays(this.remainingCheatDays)
                    .isWinner(true)
                    .joinedAt(this.joinedAt)
                    .build();
        }
    }

    /**
     * 마지막 메시지
     */
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LastMessage {
        private String content;
        private String senderId;
        private LocalDateTime createdAt;
    }

    /**
     * 새 대결 생성
     */
    public static Battle createNewBattle(String inviteCode, String hostUserId, Settings settings) {
        Map<String, Integer> unreadCount = new HashMap<>();
        unreadCount.put(hostUserId, 0);

        List<Member> members = new ArrayList<>();
        members.add(Member.createHost(hostUserId, settings.getMaxCheatDays()));

        return Battle.builder()
                .inviteCode(inviteCode)
                .status(BattleStatus.PENDING)
                .settings(settings)
                .members(members)
                .unreadCount(unreadCount)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 게스트 참여 (새 인스턴스 반환)
     */
    public Battle addGuest(String guestUserId) {
        if (this.members.size() >= 2) {
            throw new IllegalStateException("Battle already has 2 members");
        }

        List<Member> updatedMembers = new ArrayList<>(this.members);
        updatedMembers.add(Member.createGuest(guestUserId, this.settings.getMaxCheatDays()));

        Map<String, Integer> updatedUnreadCount = new HashMap<>(this.unreadCount);
        updatedUnreadCount.put(guestUserId, 0);

        return Battle.builder()
                .id(this.id)
                .inviteCode(this.inviteCode)
                .status(this.status)
                .settings(this.settings)
                .members(updatedMembers)
                .unreadCount(updatedUnreadCount)
                .lastMessage(this.lastMessage)
                .winnerId(this.winnerId)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 대결 시작 (새 인스턴스 반환)
     */
    public Battle start() {
        if (this.members.size() != 2) {
            throw new IllegalStateException("Battle needs exactly 2 members to start");
        }

        LocalDateTime now = LocalDateTime.now();

        return Battle.builder()
                .id(this.id)
                .inviteCode(this.inviteCode)
                .status(BattleStatus.IN_PROGRESS)
                .settings(this.settings)
                .members(this.members)
                .unreadCount(this.unreadCount)
                .lastMessage(this.lastMessage)
                .winnerId(this.winnerId)
                .startDate(now)
                .endDate(now.plusDays(this.settings.getDuration()))
                .createdAt(this.createdAt)
                .updatedAt(now)
                .build();
    }

    /**
     * 대결 종료 (새 인스턴스 반환)
     */
    public Battle complete(String winnerId) {
        List<Member> updatedMembers = this.members.stream()
                .map(member -> member.getUserId().equals(winnerId)
                        ? member.markAsWinner()
                        : member)
                .toList();

        return Battle.builder()
                .id(this.id)
                .inviteCode(this.inviteCode)
                .status(BattleStatus.COMPLETED)
                .settings(this.settings)
                .members(updatedMembers)
                .unreadCount(this.unreadCount)
                .lastMessage(this.lastMessage)
                .winnerId(winnerId)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 특정 사용자의 상대방 ID 조회
     */
    public String getOpponentUserId(String myUserId) {
        return members.stream()
                .map(Member::getUserId)
                .filter(id -> !id.equals(myUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Opponent not found"));
    }

    /**
     * 특정 사용자의 Member 객체 조회
     */
    public Member getMember(String userId) {
        return members.stream()
                .filter(member -> member.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Member not found"));
    }

    /**
     * Role을 기준으로 사용자의 Member 객체 조회
     */
    public Member getMemberByRole(MemberRole role) {
        return members.stream()
                .filter(member -> member.getRole().equals(role))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Member not found"));
    }
}
