package com.ex.tjspring.bookmark.controller;

import com.ex.tjspring.bookmark.dto.Bookmark;
import com.ex.tjspring.bookmark.service.BookmarkService;
import com.ex.tjspring.common.service.S3DirKey; // 추가
import com.ex.tjspring.common.service.S3Service;   // 추가
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/bookmark")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}) // 포트 추가
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private S3Service s3Service; // S3Service 추가

    private static final Logger logger = LoggerFactory.getLogger(BookmarkController.class);

    /**
     * 특정 사용자의 북마크 목록 조회 (이미지 URL 포함)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getBookmarksByUserId(
            @PathVariable Long userId
    ) {
        try {
            // 서비스를 통해 북마크 목록 조회
            List<Bookmark> bookmarks = bookmarkService.getBookmarksByUserId(userId);

            // 각 북마크에 thumbnailFullPath 설정 (GroupController와 동일한 로직)
            for (Bookmark bookmark : bookmarks) {
                String thumbnail = bookmark.getThumbnail();

                if (thumbnail == null || thumbnail.isEmpty() || thumbnail.contains("default")) {
                    // 기본 이미지 설정
                    bookmark.setThumbnailFullPath("/images/default-thumbnail.png");
                } else {
                    // S3 전체 URL 생성
                    String fullPath = s3Service.getFileFullPath(S3DirKey.STUDYGROUPIMG, thumbnail);
                    bookmark.setThumbnailFullPath(fullPath);
                }

                logger.debug("북마크 ID: {}, 썸네일: {}, 전체경로: {}",
                        bookmark.getId(), thumbnail, bookmark.getThumbnailFullPath());
            }

            // 성공 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", bookmarks);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("사용자 북마크 조회 중 오류 발생: {}", e.getMessage(), e);

            // 오류 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "북마크 목록 조회 중 오류가 발생했습니다.");

            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> addBookmark(@RequestBody Bookmark bookmark) {
        logger.info("addBookmark 호출, 받은 데이터: {}", bookmark);

        try {
            bookmarkService.addBookmark(bookmark);
            return ResponseEntity.ok(Map.of("success", true, "message", "북마크가 추가되었습니다."));
        } catch (Exception e) {
            logger.error("북마크 추가 중 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{userId}/{groupId}")
    public ResponseEntity<?> deleteBookmark(
            @PathVariable Long userId,
            @PathVariable Long groupId
    ) {
        try {
            bookmarkService.deleteBookmark(userId, groupId);
            return ResponseEntity.ok(Map.of("success", true, "message", "북마크가 삭제되었습니다."));
        } catch (Exception e) {
            logger.error("북마크 삭제 중 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/counts")
    public ResponseEntity<?> getBookmarkCounts() {
        try {
            List<Map<String, Object>> counts = bookmarkService.getBookmarkCounts();
            return ResponseEntity.ok(Map.of("success", true, "data", counts));
        } catch (Exception e) {
            logger.error("북마크 개수 조회 중 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}