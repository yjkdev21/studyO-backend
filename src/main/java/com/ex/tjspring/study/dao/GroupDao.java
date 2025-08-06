package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.GroupDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupDao {

    void insert(GroupDto groupDto);

    // 스터디 멤버십 등록 (그룹 생성자 자동 추가)
    void insertMembership(GroupDto groupDto);

    // User 테이블에서 닉네임 조회
    String selectUserNickname(@Param("userId") Long userId);

    List<GroupDto> selectAllGroups();

    void update(GroupDto groupDto);

    // 닉네임 업데이트를 위한 새로운 메서드 추가
    void updateNickname(GroupDto groupDto);

    void delete(Long id);

    GroupDto selectGroupById(Long id);

    int existsByGroupName(String groupName);

    // 새로 추가되는 메서드들
    List<GroupDto> findByUserId(@Param("userId") Long userId);

    List<GroupDto> findActiveByUserId(@Param("userId") Long userId);

    // 이미지 관련 메서드 추가
//    byte[] selectImageByFilename(@Param("filename") String filename);

}