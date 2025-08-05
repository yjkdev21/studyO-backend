package com.ex.tjspring.bookmark.service;

import com.ex.tjspring.bookmark.dto.Bookmark;
import java.util.List;

/**
 * 북마크 서비스 계층 인터페이스
 * (비즈니스 로직 처리 - 실제 구현체는 ServiceImpl에 작성)
 */
public interface BookmarkService {

    /**
     * 특정 사용자의 북마크 목록 조회
     * @param userId 사용자 ID
     * @return 북마크 목록 (스터디 정보 포함)
     */
    List<Bookmark> getBookmarksByUserId(Long userId);
}
