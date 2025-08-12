package com.ex.tjspring.study.service;

import com.ex.tjspring.study.dao.StudyMembershipDao;
import com.ex.tjspring.study.dto.StudyMembershipDto;
import com.ex.tjspring.user.model.User;
import com.ex.tjspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyMembershipService {

	private final StudyMembershipDao studyMembershipDao;

	// 멤버 추가 O
	@Transactional
	public void addMembership(Long userId, Long groupId) {
		if (userId == null || groupId == null) {
			throw new IllegalArgumentException("사용자 ID와 그룹 ID는 필수입니다.");
		}
		// 중복 가입 체크 O
		StudyMembershipDto existing = getMembershipByUserAndGroup(userId, groupId);
		if (existing != null) {
			throw new IllegalArgumentException("이미 해당 그룹에 가입된 유저입니다");
		}

		StudyMembershipDto dto = StudyMembershipDto.builder()
				.userId(userId)
				.groupId(groupId)
				.build();

		try {
			studyMembershipDao.insertMembership(dto);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("멤버십 등록 실패 :" + e.getMessage());
		}
	}





	// 사용자와 그룹으로 멤버십 조회
	public StudyMembershipDto getMembershipByUserAndGroup(Long userId, Long groupId) {
		return studyMembershipDao.selectMembershipByUserAndGroup(userId, groupId);
	}

	// 그룹의 멤버 목록 조회
	public List<StudyMembershipDto> getGroupMembers(Long groupId) {
		return studyMembershipDao.selectMembershipsByGroupId(groupId);
	}

	// 사용자의 그룹 가입이력 조회
	public List<StudyMembershipDto> getUserMemberships(Long userId) {
		return studyMembershipDao.selectMembershipsByUserId(userId);
	}


	// 닉네임 수정
	@Transactional
	public void updateNickname(Long userId, Long groupId, String nickname) {
		if (nickname == null || nickname.trim().isEmpty()) {
			throw new IllegalArgumentException("닉네임은 필수입니다.");
		}
		if (nickname.length() > 20) {
			throw new IllegalArgumentException("닉네임은 20자를 초과할 수 없습니다.");
		}

		int result = studyMembershipDao.updateNickname(userId, groupId, nickname.trim());
		if (result == 0) {
			throw new IllegalArgumentException("해당 멤버십을 찾을 수 없습니다.");
		}
	}

	// 그룹 탈퇴
	@Transactional
	public void leaveMembership(Long userId, Long groupId) {
		int result = studyMembershipDao.leaveMembership(userId, groupId);
		if (result == 0) {
			throw new IllegalArgumentException("해당 멤버십을 찾을 수 없습니다.");
		}
	}
}
