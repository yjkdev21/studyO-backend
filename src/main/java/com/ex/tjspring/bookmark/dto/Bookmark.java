package com.ex.tjspring.bookmark.dto;

import lombok.Getter;  // Getter 메소드 자동 생성
import lombok.Setter;  // Setter 메소드 자동 생성
import lombok.ToString;  // toString 메소드 자동 생성

import java.time.LocalDateTime;  // 날짜와 시간을 다루는 Java 8+ 클래스

/**
 * 북마크 데이터 전송 객체 (DTO - Data Transfer Object)
 * - 데이터베이스의 북마크 테이블과 스터디 그룹 테이블의 JOIN 결과를 담는 클래스
 * - 계층 간 데이터 전송에 사용 (Controller ↔ Service ↔ Repository)
 */
@Getter    // 모든 필드에 대해 getXxx() 메소드 자동 생성
@Setter    // 모든 필드에 대해 setXxx() 메소드 자동 생성
@ToString  // toString() 메소드 자동 생성 (디버깅 시 객체 내용 확인 용이)
public class Bookmark {

    // ========================================
    // 북마크 테이블의 기본 정보
    // ========================================
    private Long id;                    // 북마크 고유 ID (Primary Key)
    private Long userId;                // 북마크를 생성한 사용자 ID (Foreign Key)
    private Long groupId;               // 북마크된 스터디 그룹 ID (Foreign Key)
    private LocalDateTime createdAt;    // 북마크 생성 시간

    // ========================================
    // 스터디 그룹 테이블의 정보 (JOIN으로 가져온 데이터)
    // - 북마크 목록 조회 시 스터디 정보도 함께 표시하기 위해 포함
    // ========================================
    private String category;            // 스터디 카테고리
    private String groupName;           // 스터디 그룹 이름
    private String groupIntroduction;   // 스터디 그룹 소개글
    private Long groupOwnerId;          // 스터디 그룹 개설자 ID
    private LocalDateTime studyCreatedAt; // 스터디 그룹 생성 시간
    private Integer maxMembers;         // 최대 멤버 수
    private String studyMode;           // 스터디 진행 방식 (예: "온라인", "오프라인")
    private String region;              // 지역 정보 (오프라인 스터디인 경우)
    private String contact;             // 연락처 정보
    private String thumbnail;           // 스터디 그룹 썸네일 이미지 URL
}