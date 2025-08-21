package com.ex.tjspring.mainsearch.service;

import com.ex.tjspring.mainsearch.dto.SearchFilterRequest;
import com.ex.tjspring.mainsearch.model.StudyGroupModel;
import com.ex.tjspring.mainsearch.model.StudyPostModel;

import java.util.List;
import java.util.Map;

public interface SearchService {
    List<StudyGroupModel> searchStudyGroups(SearchFilterRequest filter);
    List<StudyPostModel> searchStudyPosts(SearchFilterRequest filter);
    List<Map<String, Object>> getBookmarkViewList();
    List<StudyPostModel> getPopularStudies();
    List<StudyPostModel> getUrgentStudies();
}
