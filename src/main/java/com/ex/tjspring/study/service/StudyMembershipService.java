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
		// 중복 가입 체크
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

	// 사용자와 그룹으로 멤버십 조회 O
	public StudyMembershipDto getMembershipByUserAndGroup(Long userId, Long groupId) {
		return studyMembershipDao.selectMembershipByUserAndGroup(userId, groupId);
	}

	// 그룹의 멤버 목록 조회 O
	public List<StudyMembershipDto> getGroupMembers(Long groupId) {
		return studyMembershipDao.selectMembershipsByGroupId(groupId);
	}

	// 스터디 그룹 내 닉네임 수정 O
	@Transactional
	public void updateNickname(Long userId, Long groupId, String nickname) {
		if (userId == null || userId <= 0) {
			throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");
		}
		if (groupId == null || groupId <= 0) {
			throw new IllegalArgumentException("유효하지 않은 그룹 ID입니다.");
		}
		if (nickname == null || nickname.trim().isEmpty()) {
			throw new IllegalArgumentException("닉네임은 필수입니다.");
		}
		if (nickname.length() > 20) {
			throw new IllegalArgumentException("닉네임은 20자를 초과할 수 없습니다.");
		}
		StudyMembershipDto membership = studyMembershipDao.selectMembershipByUserAndGroup(userId, groupId);
		if (membership == null) {
			throw new IllegalArgumentException("해당 그룹의 멤버가 아닙니다.");
		}
		// 닉네임 중복 체크 추가
		if (isNicknameExistsInGroup(groupId, nickname.trim(), userId)) {
			throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
		}
		int result = studyMembershipDao.updateNickname(userId, groupId, nickname.trim());
		if (result == 0) {
			throw new IllegalArgumentException("해당 멤버십을 찾을 수 없습니다.");
		}
	}

	// 그룹 내에 동일한 닉네임 있는지 확인 (중복일 경우 true)
	public boolean isNicknameExistsInGroup(Long groupId, String nickname, Long userId) {

		int count = studyMembershipDao.existsNicknameInGroup(groupId, nickname, userId);
		System.out.println("그룹ID: " + groupId + ", 닉네임: " + nickname + ", 제외할 사용자ID: " + userId + ", 중복 개수: " + count);
		if (studyMembershipDao.existsNicknameInGroup(groupId, nickname, userId) == 1) {
			return true;
		}
		return false;
	}

	// 그룹 탈퇴
	@Transactional
	public void leaveMembership(Long userId, Long groupId) {
		if (userId == null || userId <= 0) {
			throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");
		}
		if (groupId == null || groupId <= 0) {
			throw new IllegalArgumentException("유효하지 않은 그룹 ID입니다.");
		}
		StudyMembershipDto membership = studyMembershipDao.selectMembershipByUserAndGroup(userId, groupId);
		if (membership == null) {
			throw new IllegalArgumentException("해당 그룹의 멤버가 아닙니다.");
		}
		int result = studyMembershipDao.leaveMembership(userId, groupId);
		if (result == 0) {
			throw new IllegalArgumentException("해당 멤버십을 찾을 수 없습니다.");
		}

	}
}
