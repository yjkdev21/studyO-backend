package com.ex.tjspring.study.controller;

import com.ex.tjspring.common.dto.AttachFileDto;
import com.ex.tjspring.common.service.AttachFileService;
import com.ex.tjspring.common.service.S3DirKey;
import com.ex.tjspring.common.service.S3Service;
import com.ex.tjspring.editorexample.dto.ExEditorDto;
import com.ex.tjspring.editorexample.dto.ExEditorEditDto;
import com.ex.tjspring.editorexample.dto.ExEditorViewDto;
import com.ex.tjspring.editorexample.service.ExEditorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
@Slf4j
public class PromotionController {

    private final ExEditorService editorService;
    private final AttachFileService attachFileService;
    private final S3Service s3Service;

    @GetMapping(value = "/promotion")
    public ResponseEntity<Map<String, Object>> listPromotions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        try {
            Map<String, Object> response = new HashMap<>();
            int offset = paginationDataSet(page,pageSize,response);
            List<ExEditorDto> dtos = editorService.selectAllPaged(offset, pageSize);
            response.put("list", dtos);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error fetching promotion list: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "프로모션 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/promotion")
    public ResponseEntity<Long> insertPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        try {
            // 게시글 등록
            Long newPostId = insertPostToDB(title, content);

            // 첨부파일 처리
            if (attachments != null && !attachments.isEmpty()) {
                handleFileUpload(newPostId, attachments);
            }

            // newPostId 직접 반환
            return ResponseEntity.ok(newPostId);

        } catch (Exception e) {
            log.error("게시글 등록 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/promotion/{postId}")
    public ResponseEntity<ExEditorViewDto> detailPost(@PathVariable Long postId) {
        ExEditorViewDto editorViewDto = editorService.selectId(postId);

        if (editorViewDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(editorViewDto);
    }


    // 스프링은 PUT 요청에서 multipart/form-data를
    // 파싱하지 못할 수 있으며, HttpMessageNotReadableException, 500 오류가 자주 발생합니다.
    // @PutMapping("/promotion")
    @PostMapping("/promotion/edit")
    public ResponseEntity<?> updatePost( @ModelAttribute ExEditorEditDto exEditorDtoEdit ) throws IOException {

        log.info("@@@ ExEditorEditDto = {} " , exEditorDtoEdit );

        if (exEditorDtoEdit.getId() == null) {
            return ResponseEntity.badRequest().body("ID는 필수입니다.");
        }

        try {
            // 수정된 게시글 update
            updatePostToDB(exEditorDtoEdit);
            // 삭제한 attach 파일 처리
            deleteAttachFiles(exEditorDtoEdit);
            // 새로 추가된 attach file 처리
            handleFileUpload(exEditorDtoEdit.getId(), exEditorDtoEdit.getNewAttachments());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("게시글 수정 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }



    @DeleteMapping("/promotion/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {

        ExEditorViewDto editorViewDto = editorService.selectId(postId);

        if (editorViewDto == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            deletePostAttachFiles(postId);
            // 게시글을 삭제합니다.
            // (첨부파일 메타데이터는 FK cascade에 의해 자동으로 삭제됩니다.)
            editorService.delete(postId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("게시글 삭제 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/download/{storedFileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String storedFileName) {
        try {
            // 1. DB에서 storedFileName(S3 객체 키)으로 AttachFileDto 정보를 조회합니다. (원본 파일명, 파일 타입 등)
            AttachFileDto fileDto = attachFileService.selectByStoredFileName(storedFileName);

            if (fileDto == null) {
                log.warn("storedFileName {}에 대한 파일 메타데이터를 찾을 수 없습니다.", storedFileName);
                return ResponseEntity.notFound().build(); // 404 Not Found 응답을 반환합니다.
            }

            // 2. S3에서 실제 파일 데이터를 다운로드합니다.
            byte[] data = s3Service.downloadFile(S3DirKey.ATTACHFILE,fileDto.getStoredFileName());
            ByteArrayResource resource = new ByteArrayResource(data); // 바이트 배열을 리소스로 변환합니다.

            // 3. 파일명 인코딩 (한글 파일명 깨짐 방지)
            String encodedFileName = URLEncoder.encode(fileDto.getFileName(), StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

            // 4. HTTP 헤더를 설정하여 다운로드 응답을 준비합니다.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", encodedFileName); // 다운로드될 파일명을 설정합니다.
            headers.setContentType(MediaType.parseMediaType(fileDto.getFileType())); // 파일 타입을 설정합니다.
            headers.setContentLength(data.length); // 파일 크기를 설정합니다.

            // 5. ResponseEntity를 반환하여 파일 다운로드를 시작합니다.
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            // S3 다운로드 중 오류 발생 시
            log.error("storedFileName {}에 대한 S3 파일 다운로드 중 오류가 발생했습니다: {}", storedFileName, e.getMessage(), e);
            return ResponseEntity.internalServerError().build(); // 500 Internal Server Error 응답을 반환합니다.
        } catch (Exception e) {
            // 기타 예상치 못한 오류 발생 시
            log.error("storedFileName {}에 대한 파일 다운로드 중 예상치 못한 오류가 발생했습니다: {}", storedFileName, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }



    private Long insertPostToDB(String title, String content) {
        ExEditorDto exEditorDto = new ExEditorDto();
        exEditorDto.setTitle(title);
        exEditorDto.setContent(content);
        exEditorDto.setRegDate(LocalDateTime.now());
        return editorService.insert(exEditorDto);
    }

    private void updatePostToDB( ExEditorEditDto putDto) {
        // 게시글 기본 정보를 업데이트합니다.
        ExEditorDto exEditorDto = new ExEditorDto();
        exEditorDto.setId(putDto.getId());
        exEditorDto.setTitle(putDto.getTitle());
        exEditorDto.setContent(putDto.getContent());
        editorService.update(exEditorDto);
    }

    // 수정시 attachFile 개별 삭제
    private void deleteAttachFiles(ExEditorEditDto exEditorDtoEdit) {
        // 기존 첨부파일 삭제 처리 (DB 및 S3)
        List<String> deletedFileNames = exEditorDtoEdit.getDeletedStoredFileNames();
        //log.info("###### deletedFileNames={} " ,deletedFileNames);
        if (deletedFileNames != null && !deletedFileNames.isEmpty()) {
            for (String storedFileName : deletedFileNames) {
                try {
                    // S3에서 파일 삭제
                    s3Service.delete( S3DirKey.ATTACHFILE ,storedFileName);
                    // DB에서 파일 메타데이터 삭제
                    attachFileService.deleteByStoredFileName(storedFileName);
                    log.info("S3 및 DB에서 파일 삭제 완료: {}", storedFileName);
                } catch (Exception e) {
                    log.error("S3/DB에서 파일 {} 삭제 실패: {}", storedFileName, e.getMessage(), e);
                    // 부분 실패를 허용하거나, 트랜잭션 롤백 전략을 고려해야 합니다.
                }
            }
        }
    }

    // Post 삭제시 해당 포스트의 첨부파일 일괄 삭제
    private void deletePostAttachFiles(Long postId) {
        // 게시글에 연결된 첨부파일 목록을 조회합니다.
        ExEditorViewDto postToDelete = editorService.selectId(postId);
        if (postToDelete != null && postToDelete.getAttachFile() != null) {
            for (AttachFileDto file : postToDelete.getAttachFile()) {
                try {
                    // S3에서 파일 삭제
                    s3Service.delete(S3DirKey.ATTACHFILE,file.getStoredFileName());
                    log.info("게시글 {}에 연결된 S3 파일 삭제 완료: {}", postId, file.getStoredFileName());
                } catch (Exception e) {
                    log.error("게시글 {}에 연결된 S3 파일 {} 삭제 실패: {}", postId, file.getStoredFileName(), e.getMessage(), e);
                }
            }
        }
    }


    private int paginationDataSet(int page, int pageSize, Map<String, Object> response) {
        int totalCount = editorService.selectTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        // page 유효성 보정: 요청 페이지가 최대 페이지보다 크면 마지막 페이지로 조정
        if (page > totalPages && totalPages != 0) {
            page = totalPages;
        } else if (totalPages == 0) {
            page = 1;
        }

        int offset = (page - 1) * pageSize;
        int pageBlockSize = 5;
        int startPage = ((page - 1) / pageBlockSize) * pageBlockSize + 1;
        int endPage = Math.min(startPage + pageBlockSize - 1, totalPages);

        response.put("currentPage", page);
        response.put("pageSize", pageSize);
        response.put("totalCount", totalCount);
        response.put("totalPages", totalPages);
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        return offset;
    }


    // 첨부파일 추가 S3 upload
    private void handleFileUpload(Long postId, List<MultipartFile> attachments) throws IOException {

        // 새 첨부파일 추가 처리 (S3 및 DB)
        if( attachments != null && !attachments.isEmpty()) {

            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String storedFileName = s3Service.upload(S3DirKey.ATTACHFILE, file);

                    // upload 한 file url...
                    //String storedFileFullPath = s3Service.getFileFullPath(S3DirKey.ATTACHFILE, storedFileName);

                    AttachFileDto attachFileDto = new AttachFileDto();
                    attachFileDto.setPostId(postId);
                    attachFileDto.setFileName(originalFileName);
                    attachFileDto.setStoredFileName(storedFileName);
                    attachFileDto.setFileSize(file.getSize());
                    attachFileDto.setFileType(file.getContentType());
                    attachFileDto.setRegDate(LocalDateTime.now());

                    attachFileService.insert(attachFileDto);
                }
            }// for
        }
    }


}
