package com.ex.tjspring.mainsearch.mapper;

import com.ex.tjspring.mainsearch.dto.BookmarkResponse;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserBookmarkMapper {
    List<BookmarkResponse> findActiveBookmarksWithViewCount();
}
