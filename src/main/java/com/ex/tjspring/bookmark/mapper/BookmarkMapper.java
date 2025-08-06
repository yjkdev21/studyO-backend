package com.ex.tjspring.bookmark.mapper;

import com.ex.tjspring.bookmark.dto.Bookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 북마크 데이터 접근 계층 (MyBatis 매퍼)
 */
@Mapper  // MyBatis가 구현체 자동 생성
public interface BookmarkMapper {

    /**
     * 특정 사용자의 북마크 목록 조회
     * @param userId 사용자 ID
     * @return 북마크 목록 (스터디 정보 포함)
     */
    List<Bookmark> findByUserId(@Param("userId") Long userId);
    // @Param: XML의 #{userId}와 매개변수 연결
}