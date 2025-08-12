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
    // 🔥 새로 추가: 인기 스터디 API (북마크 많은 순)
    @GetMapping("/popularStudies")
    public List<StudyPostModel> getPopularStudies() {
        return searchService.getPopularStudies();
    }

    // 🔥 새로 추가: 마감임박 스터디 API (마감일 3일 전)
    @GetMapping("/urgentStudies")
    public List<StudyPostModel> getUrgentStudies() {
        return searchService.getUrgentStudies();
    }
}
