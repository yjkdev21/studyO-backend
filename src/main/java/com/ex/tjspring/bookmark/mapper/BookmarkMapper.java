package com.ex.tjspring.bookmark.mapper;

import com.ex.tjspring.bookmark.dto.Bookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;


@Mapper  // MyBatis가 구현체 자동 생성
public interface BookmarkMapper {

    List<Bookmark> findByUserId(@Param("userId") Long userId);
    void insertBookmark(Bookmark bookmark);
    void deleteBookmark(@Param("userId") Long userId, @Param("groupId") Long groupId);
    List<Map<String, Object>> getBookmarkCounts();

}