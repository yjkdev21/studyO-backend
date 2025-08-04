package com.ex.tjspring.mainsearch.controller;

import com.ex.tjspring.mainsearch.dto.BookmarkResponse;
import com.ex.tjspring.mainsearch.service.UserBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserBookmarkController {
    private final UserBookmarkService userBookmarkService;

    @GetMapping("/api/bookmarks")
    public List<BookmarkResponse> getBookmarks() {
        return userBookmarkService.getActiveBookmarks();
    }
}
