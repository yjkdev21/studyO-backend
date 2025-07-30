
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
public class ExEditorEditDto {
    @NotNull(message = "게시글 ID는 필수입니다.") // 수정 시 ID는 반드시 존재해야 함
    private Long id;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private LocalDateTime regDate; // 수정 시에는 사용되지 않지만, DTO 일관성을 위해 유지 가능

    // 새로 추가될 첨부파일 목록
    private List<MultipartFile> newAttachments; // edit.html의 name="newAttachments"와 일치

    // 삭제될 기존 첨부파일의 storedFileName 목록
    // JavaScript에서 hidden input으로 전송되는 deletedStoredFileNames와 일치
    private List<String> deletedStoredFileNames;
}
