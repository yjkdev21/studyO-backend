package com.ex.tjspring.bookmark.controller;

import com.ex.tjspring.bookmark.dto.Bookmark;
import com.ex.tjspring.bookmark.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 북마크 관련 REST API 컨트롤러
 */
@RestController  // REST API를 만들기 위한 어노테이션 (@Controller + @ResponseBody)
@RequestMapping("/api/bookmark")  // 컨트롤러의 기본 URL 경로
@CrossOrigin(origins = "http://localhost:3000")  // CORS 설정 (프론트엔드 허용)
public class BookmarkController {

    @Autowired  // Spring이 BookmarkService 객체를 자동 주입
    private BookmarkService bookmarkService;

    /**
     * 특정 사용자의 북마크 목록 조회
     * @param userId 사용자 ID (URL 경로에서 추출)
     * @return JSON 형태의 응답 데이터
     */
    @GetMapping("/user/{userId}")  // GET 요청 처리, {userId}는 경로 변수
    public ResponseEntity<Map<String, Object>> getBookmarksByUserId(
            @PathVariable Long userId  // URL 경로의 {userId} 값을 매개변수로 바인딩
    ) {
        try {
            // 서비스를 통해 북마크 목록 조회
            List<Bookmark> bookmarks = bookmarkService.getBookmarksByUserId(userId);

            // 성공 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", bookmarks);

            return ResponseEntity.ok(response);  // HTTP 200 응답

        } catch (Exception e) {
            // 오류 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(500).body(response);  // HTTP 500 응답
        }
    }
}