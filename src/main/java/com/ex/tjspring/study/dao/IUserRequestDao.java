package com.ex.tjspring.study.dao;


import com.ex.tjspring.study.dto.GroupDto;
import com.ex.tjspring.study.dto.UserRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IUserRequestDao {

	// ## 유저가 해당 스터디그룹에 가입신청한 적이 있는 지....
	int checkUserStatusForApplication(@Param("groupId") Long groupId,
									  @Param("userId") Long userId,
									  @Param("studyPostId") Long studyPostId);

    int canMaxMemberJoinStudy(Long groupId);

	// ## 가입신청할 스터디 그룹정보 가져오기..
	GroupDto selectStudyGroupFindByGroupId(Long groupId);

	// ## 가입신청 등록 ##
	int insertUserRequest(UserRequestDto dto);


	// 가입요청 리스트 O
	List<UserRequestDto> selectUserRequestFindByGroupId(Long groupId);

	// ID로 가입요청 조회 O
	UserRequestDto selectUserRequestById(Long id);

	// 가입요청 승인/거절 처리 O
	int processUserRequest(@Param("requestId") Long requestId,
						   @Param("status") String status);


	// postId 로 가입신청이 있는 지..
	int existsUserRequestByPostId(Long postId);

	// groupId 로 가입신청이 있는 지..
	int existsUserRequestByGroupId(Long groupId);


	int updateUserRequest(UserRequestDto dto);

	int deleteUserRequest(Long dto);




}
