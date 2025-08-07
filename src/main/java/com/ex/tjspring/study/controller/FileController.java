package com.ex.tjspring.file.controller; // 적절한 패키지 경로로 변경해주세요.

import com.ex.tjspring.common.service.S3DirKey;
import com.ex.tjspring.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // @RequestParam 임포트
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileController {

    private final S3Service s3Service;

    /**
     * S3에 저장된 첨부파일을 다운로드합니다.
     * 클라이언트 요청 경로: /api/file/download/{storedFileName}?originalFileName={originalFileName}
     *
     * @param storedFileName S3에 저장된 파일의 고유 이름 (UUID 등)
     * @param originalFileName 클라이언트가 전달한 원본 파일 이름 (선택 사항)
     * @return 다운로드할 파일 데이터와 HTTP 헤더를 포함한 ResponseEntity
     */
    @GetMapping("/download/{storedFileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @PathVariable String storedFileName,
            @RequestParam(value = "originalFileName", required = false) String originalFileName) {
        try {
            byte[] data = s3Service.downloadFile(S3DirKey.ATTACHFILE, storedFileName);

            // 파일이 존재하지 않는 경우
            if (data == null || data.length == 0) {
                log.warn("다운로드할 파일을 찾을 수 없습니다: {}", storedFileName);
                return ResponseEntity.notFound().build();
            }

            MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;

            // 다운로드될 파일명 결정 및 인코딩 (한글 파일명 처리)
            String fileNameToUse = (originalFileName != null && !originalFileName.isEmpty()) ? originalFileName : storedFileName;
            String encodedFileName = URLEncoder.encode(fileNameToUse, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, contentType.toString());
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length));

            // 파일 데이터를 ByteArrayResource로 래핑하여 반환
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(data));

        } catch (Exception e) {
            log.error("파일 다운로드 중 오류 발생: {}", storedFileName, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}