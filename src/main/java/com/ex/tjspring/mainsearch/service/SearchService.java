package com.ex.tjspring.mainsearch.service;

import com.ex.tjspring.mainsearch.dto.SearchFilterRequest;
import com.ex.tjspring.mainsearch.model.StudyGroupModel;
import com.ex.tjspring.mainsearch.model.StudyPostModel;

import java.util.List;
import java.util.Map;

public interface SearchService {
    List<StudyGroupModel> searchStudyGroups(SearchFilterRequest filter);
    // 새로 추가한 포스트 검색 메서드
    List<StudyPostModel> searchStudyPosts(SearchFilterRequest filter);
    List<Map<String, Object>> getBookmarkViewList();
    // 🔥 새로 추가: 인기 스터디와 마감임박 스터디
    List<StudyPostModel> getPopularStudies();
    List<StudyPostModel> getUrgentStudies();
}
