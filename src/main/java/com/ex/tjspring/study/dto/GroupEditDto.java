package com.ex.tjspring.study.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
public class GroupEditDto {
    private Long id;

    @Size(max = 6, message = "그룹명은 6자를 초과할 수 없습니다.")
    private String groupName;

    @Size(max = 6, message = "이름은 6자를 초과할 수 없습니다.")
    private String nickName;

    @Min(value = 1, message = "최대 인원은 1명 이상이어야 합니다.")
    private Long maxMembers;

    private String contact;
    private String thumbnail;
    private Long groupOwnerId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime recruit_end_date;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime study_end_date;

    private LocalDateTime created_at;

    @NotBlank(message = "200자 내외로 작성해주세요.")
    private String groupIntroduction;
}