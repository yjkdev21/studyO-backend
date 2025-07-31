package com.ex.tjspring.study.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
public class GroupInsertDto {
    @Size(max = 6, message = "그룹명은 6자를 초과할 수 없습니다.")
    private String groupName;

    @Size(max = 6, message = "이름은 6자를 초과할 수 없습니다.")
    private String nickName;

    private String category;

    @Min(value = 1, message = "최대 인원은 1명 이상이어야 합니다.")
    private Long maxMembers;

    private String studyMode;

    private String region;
    private String contact;

    @Size(max = 255, message = "그룹 소개는 255자를 초과할 수 없습니다.")
    private String groupIntroduction;

    private String thumbnailUrl;
    private Long groupOwnerId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime recruitEndDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime studyStartDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime studyEndDate;
}