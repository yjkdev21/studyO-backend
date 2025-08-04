package com.ex.tjspring.mainsearch.service;

import com.ex.tjspring.mainsearch.dto.SearchDto;
import com.ex.tjspring.mainsearch.mapper.SearchMapper;
import com.ex.tjspring.mainsearch.model.SearchModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchMapper searchMapper;

    @Override
    public List<SearchModel> findByFilters(SearchDto filter) {
        return searchMapper.findByFilters(filter);
    }
}
