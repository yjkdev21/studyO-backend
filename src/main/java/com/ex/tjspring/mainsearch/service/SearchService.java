package com.ex.tjspring.mainsearch.service;

import com.ex.tjspring.mainsearch.dto.SearchDto;
import com.ex.tjspring.mainsearch.model.SearchModel;

import java.util.List;

public interface SearchService {
    List<SearchModel> findByFilters(SearchDto filter);
}
