package com.ex.tjspring.mainsearch.mapper;

import com.ex.tjspring.mainsearch.dto.SearchDto;
import com.ex.tjspring.mainsearch.model.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SearchMapper {
    List<SearchModel> findByFilters(SearchDto filter);
}
