package com.ex.tjspring.user.mapper;

import com.ex.tjspring.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    // 사용자 생성
    Long insertUser(User user);

    // id로 사용자 조회
    User findById(Long id);
    // userId로 사용자 조회
    User findByUserId(String userId);
    // email 로 사용자 조회
    User findByEmail(String email);
    // nickname 로 사용자 조회
    User findByNickname(String nickname);
    // userId + email 로 사용자 조회
    User findByUserIdAndEmail(@Param("userId") String userId, @Param("email") String email);

	// 비밀번호 변경
	int updatePasswordById(@Param("id") Long id, @Param("password") String password);

    // 사용자 정보 업데이트
    void updateUser(User user);
    // 사용자 탈퇴 처리
    void updateIsDeleted(Long id);
}
