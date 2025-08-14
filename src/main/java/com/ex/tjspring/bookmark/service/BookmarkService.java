package com.ex.tjspring.bookmark.service;

import com.ex.tjspring.bookmark.dto.Bookmark;
import java.util.List;
import java.util.Map;

public interface BookmarkService {

    List<Bookmark> getBookmarksByUserId(Long userId);
    void addBookmark(Bookmark bookmark);
    void deleteBookmark(Long userId, Long groupId);
    List<Map<String, Object>> getBookmarkCounts();

}
