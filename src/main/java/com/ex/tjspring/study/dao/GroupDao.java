package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.GroupDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GroupDao {

    void insert(GroupDto groupDto);// 그룹 생성

    void insertMembership(GroupDto groupDto);// 스터디 멤버십 등록 (그룹 생성자 자동 추가)

    // 그룹 조회
    String selectUserNickname(@Param("userId") Long userId); //닉네임 조회

    List<GroupDto> selectAllGroups(); //모든 그룹 조회

    GroupDto selectGroupById(Long id); //ID로 특정 그룹 조회

    // 사용자 ID로 활성화 된 그룹 목록 조회
    List<GroupDto> findActiveByUserId(@Param("userId") Long userId);

    //사용자 ID로 가입한 그룹 목록 조회
    List<GroupDto> findByUserId(@Param("userId") Long userId);

    // 그룹 수정
    void update(GroupDto groupDto); //그룹 정보 수정

    void updateNickname(GroupDto groupDto); //닉네임 수정

    // 그룹 삭제
    void delete(Long id); // 그룹 전체 삭제

    void deleteMembershipsByGroupId(Long groupId); //그룹 ID로 모든 멤버십 삭제

    // 검증
    int existsByGroupName(String groupName); //그룹명 중복 확인

    int countMembersByGroupId(@Param("groupId") Long groupId); //그룹 멤버 수 조회


}
