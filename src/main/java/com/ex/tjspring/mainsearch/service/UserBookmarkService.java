package com.ex.tjspring.mainsearch.service;

import com.ex.tjspring.mainsearch.dto.BookmarkResponse;
import com.ex.tjspring.mainsearch.mapper.UserBookmarkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBookmarkService {
    private final UserBookmarkMapper userBookmarkMapper;

    public List<BookmarkResponse> getActiveBookmarks() {
        return userBookmarkMapper.findActiveBookmarksWithViewCount();
    }
}
