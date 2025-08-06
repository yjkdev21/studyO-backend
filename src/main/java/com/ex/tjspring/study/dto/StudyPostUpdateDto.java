package com.ex.tjspring.editorexample.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // @NotNull 임포트
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudyPostUpdateDto {

    @NotNull(message = "게시글 ID는 필수입니다.") // 수정 시 ID는 반드시 존재해야 함
    private Long studyPostId;

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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int viewCount;

    @NotBlank(message = "해쉬태그 내용을 입력해주세요. 예) #자격증#공인중개사")
    private String hashTag;

    // 새로 추가될 첨부파일 목록
    private List<MultipartFile> newAttachments; // edit.html의 name="newAttachments"와 일치

    // 삭제될 기존 첨부파일의 storedFileName 목록
    // JavaScript 에서 hidden input 으로 전송되는 deletedStoredFileNames와 일치
    private List<String> deletedStoredFileNames;
}
