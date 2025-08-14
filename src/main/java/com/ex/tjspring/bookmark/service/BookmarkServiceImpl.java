package com.ex.tjspring.bookmark.service;

import com.ex.tjspring.bookmark.dto.Bookmark;
import com.ex.tjspring.bookmark.mapper.BookmarkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class BookmarkServiceImpl implements BookmarkService {

    @Autowired  // BookmarkMapper를 자동 주입받음
    private BookmarkMapper bookmarkMapper;

    @Override
    public List<Bookmark> getBookmarksByUserId(Long userId) {
        // Mapper를 통해 DB에서 해당 사용자의 북마크 목록 조회
        return bookmarkMapper.findByUserId(userId);
    }
    @Override
    public void addBookmark(Bookmark bookmark) {
        if (bookmark.getGroupId() == null) {
            bookmark.setGroupId(0L);
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
