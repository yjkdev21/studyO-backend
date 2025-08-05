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
    // study_group_stats 또는 다른 테이블에서 GROUP_ID별 조회수, 북마크 수 반환
    List<Map<String, Object>> getBookmarkViewList();
}
