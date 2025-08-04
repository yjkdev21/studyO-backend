package com.ex.tjspring.mainsearch.controller;

import com.ex.tjspring.mainsearch.dto.SearchDto;
import com.ex.tjspring.mainsearch.model.SearchModel;
import com.ex.tjspring.mainsearch.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    // GET /api/search?category=IT&mode=온라인&... 필터 쿼리 파라미터로 받음
    @GetMapping
    public List<SearchModel> searchStudyGroups(SearchDto filter) {
        return searchService.findByFilters(filter);
    }
}
