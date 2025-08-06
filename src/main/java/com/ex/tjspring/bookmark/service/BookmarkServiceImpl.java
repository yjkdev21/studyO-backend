package com.ex.tjspring.bookmark.service;

import com.ex.tjspring.bookmark.dto.Bookmark;
import com.ex.tjspring.bookmark.mapper.BookmarkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 북마크 서비스 구현 클래스
 * (비즈니스 로직 처리 - Mapper를 이용해 DB 접근)
 */
@Service  // 스프링이 이 클래스를 서비스 빈으로 등록함
public class BookmarkServiceImpl implements BookmarkService {

    @Autowired  // BookmarkMapper를 자동 주입받음
    private BookmarkMapper bookmarkMapper;

    /**
     * 특정 사용자의 북마크 목록 조회
     * @param userId 사용자 ID
     * @return 북마크 목록 (스터디 정보 포함)
     */
    @Override
    public List<Bookmark> getBookmarksByUserId(Long userId) {
        // Mapper를 통해 DB에서 해당 사용자의 북마크 목록 조회
        return bookmarkMapper.findByUserId(userId);
    }
    @Override
    public void addBookmark(Bookmark bookmark) {
        if (bookmark.getGroupId() == null) {
            bookmark.setGroupId(0L); // 예시, 실제 DB가 허용하는 기본값으로
        }
        bookmarkMapper.insertBookmark(bookmark);
    }


    @Override
    public void deleteBookmark(Long userId, Long groupId) {
        bookmarkMapper.deleteBookmark(userId, groupId);
    }
    @Override
    public List<Map<String, Object>> getBookmarkCounts() {
        return bookmarkMapper.getBookmarkCounts();
    }

}
