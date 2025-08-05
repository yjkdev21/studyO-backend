package com.ex.tjspring.mainsearch.controller;

import com.ex.tjspring.mainsearch.dto.SearchFilterRequest;
import com.ex.tjspring.mainsearch.model.StudyGroupModel;
import com.ex.tjspring.mainsearch.model.StudyPostModel;
import com.ex.tjspring.mainsearch.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/search")
    public List<StudyGroupModel> searchStudyGroups(SearchFilterRequest filter) {
        return searchService.searchStudyGroups(filter);
    }
    @GetMapping("/searchPosts")
    public List<StudyPostModel> searchStudyPosts(SearchFilterRequest filter) {
        return searchService.searchStudyPosts(filter);
    }

    @GetMapping("/bookmarks")
    public List<Map<String, Object>> getBookmarkViewList() {
        return searchService.getBookmarkViewList();
    }
}
