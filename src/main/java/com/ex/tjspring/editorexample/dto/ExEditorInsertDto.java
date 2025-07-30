package com.ex.tjspring.editorexample.dto;

import jakarta.validation.constraints.NotBlank; // @NotBlank로 변경하여 null, 빈 문자열, 공백만 있는 경우 모두 검증
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile; // 파일 업로드를 위해 MultipartFile 임포트

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExEditorInsertDto {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
    private LocalDateTime regDate;
    private List<MultipartFile> attachments; // insert.html의 name="attachments"와 일치
}
