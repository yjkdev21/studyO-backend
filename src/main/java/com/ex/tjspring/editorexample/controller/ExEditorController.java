package com.ex.tjspring.editorexample.controller;

import com.ex.tjspring.common.dto.AttachFileDto; // 첨부파일 DTO 임포트
import com.ex.tjspring.common.service.S3DirKey;
import com.ex.tjspring.editorexample.dto.ExEditorDto; // 게시글 DB 저장용 DTO
import com.ex.tjspring.editorexample.dto.ExEditorInsertDto; // 폼에서 데이터 바인딩용 DTO
import com.ex.tjspring.editorexample.dto.ExEditorViewDto; // 게시글 상세 조회용 DTO
import com.ex.tjspring.editorexample.dto.ExEditorEditDto; // 게시글 수정 폼 데이터 바인딩용 DTO


import com.ex.tjspring.common.service.AttachFileService; // AttachFileService 임포트
import com.ex.tjspring.common.service.S3Service; // S3Service 임포트
import com.ex.tjspring.editorexample.service.ExEditorService; // ExEditorService 임포트

import lombok.extern.slf4j.Slf4j; // Slf4j 임포트
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource; // ByteArrayResource 임포트
import org.springframework.http.HttpHeaders; // HttpHeaders 임포rt
import org.springframework.http.MediaType; // MediaType 임포트
import org.springframework.http.ResponseEntity; // ResponseEntity 임포트
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // @RequestParam 임포트
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder; // URLEncoder 임포트
import java.nio.charset.StandardCharsets; // StandardCharsets 임포트
import java.time.LocalDateTime; // LocalDateTime 임포트
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/editorexample")
public class ExEditorController {

    @Autowired // ExEditorService 빈을 자동으로 주입합니다.
    private ExEditorService editorService;
    @Autowired // AttachFileService 빈을 자동으로 주입합니다.
    private AttachFileService attachFileService;
    @Autowired // S3Service 빈을 자동으로 주입합니다.
    private S3Service s3Service;

    // 새 게시글 작성 폼을 보여주는 GET 요청을 처리합니다.
    @GetMapping("/insert")
    public String createPostForm(Model model) {
        // 폼 바인딩을 위해 빈 ExEditorDtoInsert 객체를 모델에 추가합니다.
        model.addAttribute("exEditorDtoInsert", new ExEditorInsertDto());
        return "/editorexample/insert"; // insert.html 템플릿의 경로를 반환합니다.
    }

    // 새 게시글을 등록하는 POST 요청을 처리합니다. (유효성 검증 및 파일 업로드 포함)
    @PostMapping("/insert")
    public String insertPost(
            @Validated ExEditorInsertDto exEditorDtoInsert, // 폼 데이터 바인딩 및 유효성 검증을 수행합니다.
            BindingResult bindingResult, // 유효성 검증 결과를 담는 객체입니다.
            RedirectAttributes redirectAttributes, // 리다이렉트 시 데이터를 전달하는 데 사용됩니다.
            Model model) { // 뷰에 데이터를 전달하는 데 사용됩니다.

        log.info("insertPost--------"); // 메서드 시작 로그

        // 1. 유효성 검증 실패 시
        if (bindingResult.hasErrors()) {
            // 오류가 있다면 insert.html 폼으로 다시 돌아가 오류 메시지를 표시합니다.
            // exEditorDtoInsert 객체는 자동으로 모델에 다시 추가되어 폼 필드에 입력했던 값을 유지합니다.
            return "/editorexample/insert";
        }

        try {
            // 2. 게시글 데이터 준비 (DB 저장용 DTO로 변환)
            ExEditorDto exEditorDto = new ExEditorDto();
            exEditorDto.setTitle(exEditorDtoInsert.getTitle());
            exEditorDto.setContent(exEditorDtoInsert.getContent());
            exEditorDto.setRegDate(LocalDateTime.now()); // 게시글 등록일은 현재 시간으로 설정합니다.

            // 3. 게시글 데이터를 먼저 DB에 저장하고 생성된 ID를 받습니다.
            // ExEditorService의 insert 메서드는 저장 후 생성된 ID를 exEditorDto의 id 필드에 설정합니다.
            Long newPostId = editorService.insert(exEditorDto); // 서비스 메서드를 호출하여 게시글을 삽입하고 ID를 가져옵니다.

            log.info("newPostId = {} ", newPostId); // 생성된 게시글 ID 로그
            log.info("exEditorDto = {} ", exEditorDto); // 게시글 DTO 로그

            // 4. 첨부파일 처리
            List<MultipartFile> attachments = exEditorDtoInsert.getAttachments();

            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) { // 파일이 비어있지 않은 경우에만 처리합니다.
                    // S3에 파일 업로드 및 저장된 파일명(S3 객체 키)을 받습니다.
                    String originalFileName = file.getOriginalFilename();
                    String storedFileName = s3Service.upload(S3DirKey.ATTACHFILE.getDirKeyName(),file); // S3Service가 S3 객체 키를 반환합니다.

                    // AttachFileDto를 생성하고 파일 메타데이터를 설정합니다.
                    AttachFileDto attachFileDto = new AttachFileDto();
                    attachFileDto.setPostId(newPostId); // 새로 생성된 게시글 ID와 연결합니다.
                    attachFileDto.setFileName(originalFileName);
                    attachFileDto.setStoredFileName(storedFileName); // S3에 저장된 객체 키를 저장합니다.
                    attachFileDto.setFileSize(file.getSize());
                    attachFileDto.setFileType(file.getContentType());
                    attachFileDto.setRegDate(LocalDateTime.now());

                    // 5. 첨부파일 메타데이터를 DB에 저장합니다.
                    attachFileService.insert(attachFileDto);
                }
            }

            // 게시글 등록 성공 메시지를 리다이렉트 시 전달합니다.
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 등록되었습니다!");
            return "redirect:/editorexample/list";

        } catch (IOException e) {
            // 파일 처리 중 오류 발생 시
            log.error("파일 업로드 중 오류가 발생했습니다: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
            return "/editorexample/insert";
        } catch (Exception e) {
            // 기타 데이터베이스 삽입 중 오류 발생 시
            log.error("게시글 등록 중 오류가 발생했습니다: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "게시글 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "/editorexample/insert";
        }
    }

    // 게시글 상세 보기를 처리하는 GET 요청을 처리합니다.
    @GetMapping("/view/{id}") // URL 경로에서 게시글 ID를 받습니다.
    public String viewPost(@PathVariable Long id, // URL 경로 변수에서 게시글 ID를 추출합니다.
                           @RequestParam(defaultValue = "1") int page, // 목록으로 돌아갈 때 현재 페이지 정보를 유지합니다.
                           @RequestParam(defaultValue = "10") int pageSize, // 목록으로 돌아갈 때 페이지 크기 정보를 유지합니다.
                           RedirectAttributes redirectAttributes,
                           Model model) {
        try {
            // EditorService를 통해 게시글 상세 정보 (첨부파일 포함)를 조회합니다.
            ExEditorViewDto editorViewDto = editorService.selectId(id);

            if (editorViewDto == null) {
                redirectAttributes.addFlashAttribute("message", "요청하신 게시글을 찾을 수 없습니다.");
                return "redirect:/editorexample/list";
            }

            // 현재 페이지 및 페이지 크기 정보를 모델에 추가합니다.
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("dto", editorViewDto); // 조회된 DTO를 모델에 추가합니다.
            log.info("View Post DTO: {}", editorViewDto); // 상세 게시글 DTO 로그
            return "/editorexample/view";
        } catch (Exception e) {
            // 게시글 상세 정보 조회 중 오류 발생 시
            log.error("ID {}에 대한 게시글 상세 정보를 불러오는 중 오류가 발생했습니다: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "게시글 상세 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/editorexample/list"; // 오류 발생 시 목록 페이지로 리다이렉트합니다.
        }
    }

    // 첨부파일 다운로드를 처리하는 GET 요청을 처리합니다.
    @GetMapping("/download/{storedFileName}") // URL 경로에서 S3 객체 키를 받습니다.
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String storedFileName) {
        try {
            // 1. DB에서 storedFileName(S3 객체 키)으로 AttachFileDto 정보를 조회합니다. (원본 파일명, 파일 타입 등)
            AttachFileDto fileDto = attachFileService.selectByStoredFileName(storedFileName);

            if (fileDto == null) {
                log.warn("storedFileName {}에 대한 파일 메타데이터를 찾을 수 없습니다.", storedFileName);
                return ResponseEntity.notFound().build(); // 404 Not Found 응답을 반환합니다.
            }

            // 2. S3에서 실제 파일 데이터를 다운로드합니다.
            byte[] data = s3Service.downloadFile(S3DirKey.ATTACHFILE.getDirKeyName(),fileDto.getStoredFileName());
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

    // 게시글 목록 조회를 처리하는 GET 요청을 처리합니다. (페이징 적용)
    @GetMapping(value = "/list")
    public String listPosts(
            @RequestParam(defaultValue = "1") int page, // 현재 페이지 번호를 받습니다. 기본값은 1입니다.
            @RequestParam(defaultValue = "10") int pageSize, // 페이지당 게시글 수를 받습니다. 기본값은 10입니다.
            Model model) {

        pageSize = 5;

        try {
            int offset = (page - 1) * pageSize; // 데이터베이스 쿼리를 위한 오프셋을 계산합니다.
            List<ExEditorDto> dtos = editorService.selectAllPaged(offset, pageSize); // 페이징된 게시글 목록을 조회합니다.
            int totalCount = editorService.selectTotalCount(); // 전체 게시글 수를 조회합니다.
            int totalPages = (int) Math.ceil((double) totalCount / pageSize); // 전체 페이지 수를 계산합니다.
            int pageBlockSize = 5; // 페이지 번호 블록의 크기를 설정합니다. (예: 1 2 3 4 5)
            int startPage = ((page - 1) / pageBlockSize) * pageBlockSize + 1; // 현재 페이지 블록의 시작 페이지 번호를 계산합니다.
            int endPage = Math.min(startPage + pageBlockSize - 1, totalPages); // 현재 페이지 블록의 끝 페이지 번호를 계산합니다.

            // 조회된 데이터를 모델에 추가하여 뷰로 전달합니다.
            model.addAttribute("list", dtos);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);

            // insertPost에서 전달된 메시지가 있다면 모델에 추가합니다. (Flash Attribute)
            if (model.containsAttribute("message")) {
                model.addAttribute("message", model.getAttribute("message"));
            }

            return "/editorexample/list"; // list.html 템플릿의 경로를 반환합니다.
        } catch (Exception e) {
            // 게시글 목록 조회 중 오류 발생 시
            log.error("게시글 목록을 불러오는 중 오류가 발생했습니다: {}", e.getMessage(), e);
            model.addAttribute("message", "게시글 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return "/editorexample/error"; // 에러 페이지로 리다이렉트하거나 에러 메시지를 표시합니다.
        }
    }

    // 게시글 수정 폼을 보여주는 GET 요청을 처리합니다.
    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        try {
            // EditorService를 통해 게시글 정보와 첨부파일 목록을 조회합니다.
            ExEditorViewDto dto = editorService.selectId(id);
            if (dto == null) {
                model.addAttribute("message", "수정할 게시글을 찾을 수 없습니다.");
                return "redirect:/editorexample/list";
            }
            model.addAttribute("dto", dto); // 조회된 EditorViewDto를 모델에 추가합니다.
            return "/editorexample/edit"; // edit.html 템플릿의 경로를 반환합니다.
        } catch (Exception e) {
            log.error("ID {}에 대한 게시글 수정 폼을 불러오는 중 오류가 발생했습니다: {}", id, e.getMessage(), e);
            model.addAttribute("message", "게시글 수정 폼을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/editorexample/list";
        }
    }

    // 게시글을 수정하는 POST 요청을 처리합니다.
    @PostMapping("/edit")
    public String updatePost(
            @Validated ExEditorEditDto exEditorDtoEdit, // 수정 폼 데이터 바인딩 및 유효성 검증을 수행합니다.
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        //log.info("updatePost--------"); // 메서드 시작 로그
        //log.info("ExEditorDtoEdit: {}", exEditorDtoEdit); // 수정 DTO 로그

        // 1. 유효성 검증 실패 시
        if (bindingResult.hasErrors()) {
            // 유효성 검증 실패 시 기존 데이터와 함께 edit 폼으로 돌아가야 합니다.
            try {
                // 현재 DTO에 있는 ID로 다시 게시글 정보를 조회하여 모델에 추가합니다.
                ExEditorViewDto originalDto = editorService.selectId(exEditorDtoEdit.getId());
                model.addAttribute("dto", originalDto);
            } catch (Exception e) {
                log.error("유효성 검증 오류 후 게시글 재조회 중 오류 발생: {}", exEditorDtoEdit.getId(), e);
                redirectAttributes.addFlashAttribute("message", "게시글 수정 중 오류가 발생했습니다: " + e.getMessage());
                return "redirect:/editorexample/list";
            }
            return "/editorexample/edit";
        }

        try {
            // 2. 게시글 기본 정보를 업데이트합니다.
            ExEditorDto exEditorDto = new ExEditorDto();
            exEditorDto.setId(exEditorDtoEdit.getId());
            exEditorDto.setTitle(exEditorDtoEdit.getTitle());
            exEditorDto.setContent(exEditorDtoEdit.getContent());
            // regDate는 수정 시 변경하지 않으므로 설정하지 않습니다. (DB에서 관리)
            editorService.update(exEditorDto);

            // 3. 기존 첨부파일 삭제 처리 (DB 및 S3)
            List<String> deletedFileNames = exEditorDtoEdit.getDeletedStoredFileNames();

            log.info("###### deletedFileNames={} " ,deletedFileNames);
            if (deletedFileNames != null && !deletedFileNames.isEmpty()) {
                for (String storedFileName : deletedFileNames) {
                    try {
                        // S3에서 파일 삭제
                        s3Service.delete( S3DirKey.ATTACHFILE.getDirKeyName() ,storedFileName);
                        // DB에서 파일 메타데이터 삭제
                        attachFileService.deleteByStoredFileName(storedFileName);
                        log.info("S3 및 DB에서 파일 삭제 완료: {}", storedFileName);
                    } catch (Exception e) {
                        log.error("S3/DB에서 파일 {} 삭제 실패: {}", storedFileName, e.getMessage(), e);
                        // 부분 실패를 허용하거나, 트랜잭션 롤백 전략을 고려해야 합니다.
                    }
                }
            }

            // 4. 새 첨부파일 추가 처리 (S3 및 DB)
            List<MultipartFile> newAttachments = exEditorDtoEdit.getNewAttachments();
            if (newAttachments != null && !newAttachments.isEmpty()) {
                for (MultipartFile file : newAttachments) {
                    if (!file.isEmpty()) {
                        String originalFileName = file.getOriginalFilename();
                        String storedFileName = s3Service.upload(S3DirKey.ATTACHFILE.getDirKeyName(),file); // S3에 업로드

                        AttachFileDto attachFileDto = new AttachFileDto();
                        attachFileDto.setPostId(exEditorDtoEdit.getId()); // 수정하는 게시글 ID와 연결합니다.
                        attachFileDto.setFileName(originalFileName);
                        attachFileDto.setStoredFileName(storedFileName);
                        attachFileDto.setFileSize(file.getSize());
                        attachFileDto.setFileType(file.getContentType());
                        attachFileDto.setRegDate(LocalDateTime.now());

                        attachFileService.insert(attachFileDto); // DB에 메타데이터 저장
                        log.info("새 파일 S3 업로드 및 DB 저장 완료: {}", originalFileName);
                    }
                }
            }

            // 게시글 수정 성공 메시지를 리다이렉트 시 전달합니다.
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다!");
            return "redirect:/editorexample/view/" + exEditorDtoEdit.getId(); // 수정된 게시글 상세 페이지로 리다이렉트합니다.

        } catch (IOException e) {
            log.error("게시글 업데이트 중 파일 처리 오류: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "파일 처리 중 오류가 발생했습니다: " + e.getMessage());
            // 오류 발생 시 기존 데이터와 함께 edit 폼으로 돌아가야 합니다.
            try {
                model.addAttribute("dto", editorService.selectId(exEditorDtoEdit.getId()));
            } catch (Exception ex) {
                log.error("IOException 발생 후 게시글 재조회 중 오류: {}", exEditorDtoEdit.getId(), ex);
            }
            return "/editorexample/edit";
        } catch (Exception e) {
            log.error("ID {} 게시글 업데이트 중 오류 발생: {}", exEditorDtoEdit.getId(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "게시글 수정 중 오류가 발생했습니다: " + e.getMessage());
            // 오류 발생 시 기존 데이터와 함께 edit 폼으로 돌아가야 합니다.
            try {
                model.addAttribute("dto", editorService.selectId(exEditorDtoEdit.getId()));
            } catch (Exception ex) {
                log.error("일반 예외 발생 후 게시글 재조회 중 오류: {}", exEditorDtoEdit.getId(), ex);
            }
            return "/editorexample/edit";
        }
    }

    // 게시글 삭제를 처리하는 POST 요청을 처리합니다.
    @PostMapping(value="/delete/{id}")
    public String deletePost(@PathVariable Long id, RedirectAttributes rttr) {
        try {
            // 1. 게시글에 연결된 첨부파일 목록을 조회합니다.
            ExEditorViewDto postToDelete = editorService.selectId(id);
            if (postToDelete != null && postToDelete.getAttachFile() != null) {
                for (AttachFileDto file : postToDelete.getAttachFile()) {
                    try {
                        // S3에서 파일 삭제
                        s3Service.delete(S3DirKey.ATTACHFILE.getDirKeyName(),file.getStoredFileName());
                        // DB에서 첨부파일 메타데이터 삭제 (on delete cascade 외래 키 제약 조건에 의해 게시글 삭제 시 자동 삭제되지만, S3 삭제는 수동으로 처리합니다.)
                        // attachFileService.deleteByStoredFileName(file.getStoredFileName()); // FK cascade로 자동 삭제되므로 이 라인은 주석 처리합니다.
                        log.info("게시글 {}에 연결된 S3 파일 삭제 완료: {}", id, file.getStoredFileName());
                    } catch (Exception e) {
                        log.error("게시글 {}에 연결된 S3 파일 {} 삭제 실패: {}", id, file.getStoredFileName(), e.getMessage(), e);
                        // S3 삭제 실패 시에도 게시글 삭제는 진행하도록 하거나, 롤백 전략을 고려해야 합니다.
                    }
                }
            }

            // 2. 게시글을 삭제합니다. (첨부파일 메타데이터는 FK cascade에 의해 자동으로 삭제됩니다.)
            editorService.delete(id);
            rttr.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다!");
            return "redirect:/editorexample/list"; // 삭제 후 목록 페이지로 리다이렉트합니다.

        } catch (Exception e) {
            log.error("ID {} 게시글 삭제 중 오류 발생: {}", id, e.getMessage(), e);
            rttr.addFlashAttribute("message", "게시글 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/editorexample/view/" + id; // 삭제 실패 시 상세 페이지로 리다이렉트합니다.
        }
    }
}
