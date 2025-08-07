package com.ex.tjspring.editorexample.dto;

import com.ex.tjspring.common.dto.AttachFileDto;
import com.ex.tjspring.study.dto.StudyPostDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat; // 이 임포트가 중요합니다!
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudyPostUpdateDto {
    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long studyPostId;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotNull(message = "User ID는 필수입니다.")
    private Long authorId;

    @NotNull(message = "모집 시작일을 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy.MM.dd") // <-- 이 어노테이션 추가
    private LocalDateTime recruitStartDate;

    @NotNull(message = "모집 종료일을 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy.MM.dd") // <-- 이 어노테이션 추가
    private LocalDateTime recruitEndDate;

    @NotBlank(message = "해쉬태그 내용을 입력해주세요. 예) #자격증#공인중개사")
    private String hashTag;

    private List<String> deletedStoredFileNames;
    private List<MultipartFile> newAttachments;

    public StudyPostDto toStudyPostDto() {
        StudyPostDto dto = new StudyPostDto();
        dto.setStudyPostId(this.studyPostId);
        dto.setTitle(this.title);
        dto.setContent(this.content);
        dto.setAuthorId(this.authorId);
        dto.setRecruitStartDate(this.recruitStartDate);
        dto.setRecruitEndDate(this.recruitEndDate);
        dto.setHashTag(this.hashTag);
        return dto;
    }
}