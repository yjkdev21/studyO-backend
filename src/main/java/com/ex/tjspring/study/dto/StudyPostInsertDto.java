package com.ex.tjspring.editorexample.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudyPostInsertDto {

    private Long studyPostId;

    @NotNull(message = "그룹 ID는 필수입니다.")
    private Long groupId;

    @NotNull(message = "User ID는 필수입니다.")
    private Long authorId;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotNull(message = "모집 시작일을 입력해주세요.")
    private LocalDateTime recruitStartDate;

    @NotNull(message = "모집 종료일을 입력해주세요.")
    private LocalDateTime recruitEndDate;

    private LocalDateTime updatedAt;

    private int viewCount;

    @NotBlank(message = "해쉬태그 내용을 입력해주세요. 예) #자격증#공인중개사")
    private String hashTag;

    private LocalDateTime createdAt;
    private List<MultipartFile> attachments; // insert.html의 name="attachments"와 일치
}
