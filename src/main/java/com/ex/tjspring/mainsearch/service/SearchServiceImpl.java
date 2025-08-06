package com.ex.tjspring.mainsearch.service;

import com.ex.tjspring.mainsearch.dto.SearchFilterRequest;
import com.ex.tjspring.mainsearch.mapper.SearchMapper;
import com.ex.tjspring.mainsearch.model.StudyGroupModel;
import com.ex.tjspring.mainsearch.model.StudyPostModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final SearchMapper searchMapper;


    @Override
    public List<StudyGroupModel> searchStudyGroups(SearchFilterRequest filter) {
        return searchMapper.searchStudyGroups(filter);
    }

    @Override
    public List<StudyPostModel> searchStudyPosts(SearchFilterRequest filter) {
        System.out.println("searchStudyPosts 호출, 필터: " + filter);
        List<StudyPostModel> result = searchMapper.searchStudyPosts(filter);
        System.out.println("쿼리 결과 건수: " + result.size());
        return result;
    }

    @Override
    public List<Map<String, Object>> getBookmarkViewList() {
        return searchMapper.getBookmarkViewList();
    }
}
