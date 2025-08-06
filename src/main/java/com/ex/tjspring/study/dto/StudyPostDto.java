package com.ex.tjspring.study.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter // 필요한 필드에 대한 Getter만 명시적으로
@Setter
@NoArgsConstructor // 기본 생성자는 MyBatis 매핑에 필수
@AllArgsConstructor // 모든 필드를 포함하는 생성자
@Builder // 빌더 패턴으로 객체 생성
public class StudyPostDto {
    private Long studyPostId;
    private String title;
    private String content;
    private Long groupId;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime recruitStartDate;
    private LocalDateTime recruitEndDate;
    private LocalDateTime updatedAt;
    private int viewCount;
    private String hashTag;
}
