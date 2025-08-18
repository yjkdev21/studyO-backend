package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.StudyMembershipDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface StudyMembershipDao {
	// 멤버십 등록
	int insertMembership(StudyMembershipDto membership);

	// 사용자 ID와 그룹 ID로 멤버십 조회 (중복 체크용)
	StudyMembershipDto selectMembershipByUserAndGroup(@Param("userId") Long userId,
													  @Param("groupId") Long groupId);

	// 그룹 ID로 멤버십 목록 조회(그룹의 멤버 가입이력)
	List<StudyMembershipDto> selectMembershipsByGroupId(@Param("groupId") Long groupId);

	// 스터디 그룹 내 닉네임 수정
	int updateNickname(@Param("userId") Long userId,
					   @Param("groupId") Long groupId,
					   @Param("nickname") String nickname);

	// 스터디 내 동일 닉네임 여부
	int existsNicknameInGroup (
			@Param("groupId") Long groupId,
			@Param("nickname") String nickname,
			@Param("excludeUserId") Long userId
	);

	// 그룹 탈퇴 처리
	int leaveMembership(@Param("userId") Long userId,
						@Param("groupId") Long groupId);


	// 사용자 ID로 멤버십 목록 조회(사용자의 그룹 가입이력)
	List<StudyMembershipDto> selectMembershipsByUserId(@Param("userId") Long userId);

}
