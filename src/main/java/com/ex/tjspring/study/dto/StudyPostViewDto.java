package com.ex.tjspring.study.dto;

import com.ex.tjspring.common.dto.AttachFileDto;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter // 필요한 필드에 대한 Getter만 명시적으로
@Setter
@NoArgsConstructor // 기본 생성자는 MyBatis 매핑에 필수
@AllArgsConstructor // 모든 필드를 포함하는 생성자
@Builder // 빌더 패턴으로 객체 생성
public class StudyPostViewDto {
    private Long studyPostId;
    private String title;
    private String content;
    private Long groupId;
    private Long authorId;
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    private LocalDateTime createdAt;
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    private LocalDateTime recruitStartDate;
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    private LocalDateTime recruitEndDate;
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    private LocalDateTime updatedAt;
    private int viewCount;
    private String hashTag;
    private String profileImage;

    List<AttachFileDto> attachFile;
}
