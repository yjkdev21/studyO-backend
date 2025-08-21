package com.ex.tjspring.mainsearch.mapper;

import com.ex.tjspring.mainsearch.dto.SearchFilterRequest;
import com.ex.tjspring.mainsearch.model.StudyGroupModel;
import com.ex.tjspring.mainsearch.model.StudyPostModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SearchMapper {
    List<StudyGroupModel> searchStudyGroups(SearchFilterRequest filter);
    List<StudyPostModel> searchStudyPosts(SearchFilterRequest filter);
    List<Map<String, Object>> getBookmarkViewList();
    List<StudyPostModel> selectPopularStudies();
    List<StudyPostModel> selectUrgentStudies();
}
