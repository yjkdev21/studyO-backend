// src/main/java/com/ex/tjspring/admin/mapper/AdminMapper.java
package com.ex.tjspring.admin.mapper;

import com.ex.tjspring.admin.model.UserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ex.tjspring.admin.model.StudyGroupModel;
import java.util.List;

@Mapper
public interface AdminMapper {
    List<UserModel> findAllUsers();
    List<UserModel> searchUsers(@Param("searchKeyword") String searchKeyword);
    // 회원 상세 정보를 가져오는 메서드
    UserModel findUserByUserId(@Param("userId") String userId);

    // 회원이 속한 스터디 목록을 가져오는 메서드
    List<StudyGroupModel> findUserStudyGroups(@Param("userId") String userId);

    // 회원 탈퇴 처리 메서드
    void deleteUser(@Param("userId") String userId);
}