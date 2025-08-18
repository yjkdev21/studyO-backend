package com.ex.tjspring.study.service;

import com.ex.tjspring.study.dao.IUserRequestDao;
import com.ex.tjspring.study.dto.GroupDto;
import com.ex.tjspring.study.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRequestService {

    private final IUserRequestDao dao;
    private final StudyMembershipService studyMembershipService;


    // ## 유저가 해당 스터디그룹에 가입신청한 적이 있는 지....
    @Transactional
    public String checkUserStatusForApplication(Long groupId, Long userId, Long studyPostId  ) {
        String result = "joinAble";
        if( dao.checkUserStatusForApplication(groupId, userId, studyPostId) > 0 ){
            if(!canMaxMemberJoinStudy(groupId)){
                result = "모집 정원이 마감되었습니다."; // 모집정원 초과 확인
            }
            return result; // 가입가능..
        } else {
            return "이미 가입신청한 스터디 그룹입니다.";
        }
    }

    // ## 가입인원 초과 확인 - 가입가능 true
    public boolean canMaxMemberJoinStudy(Long groupId) {
        return dao.canMaxMemberJoinStudy(groupId) > 0;
    }

    // ## 가입신청할 스터디 그룹정보 가져오기..
    public GroupDto selectStudyGroupFindByGroupId(Long groupId) {
        return dao.selectStudyGroupFindByGroupId(groupId);
    }

    // ## 가입 신청 ##
    public int insertUserRequest(UserRequestDto dto) {
        return dao.insertUserRequest(dto);
    }


    // GroupId 로 신청목록 가져오기 O
    public List<UserRequestDto> selectUserRequestFindByGroupId(Long groupId) {
        return dao.selectUserRequestFindByGroupId(groupId);
    }






    // postId 로 가입신청이 있는 지..
    public boolean existsUserRequestByPostId(Long postId) {
        return dao.existsUserRequestByPostId( postId ) > 0;
    }

    // groupId 로 가입신청이 있는 지..
    public boolean existsUserRequestByGroupId(Long groupId) {
        return dao.existsUserRequestByGroupId( groupId ) > 0;
    }


    public int updateUserRequest(UserRequestDto dto) {
        return dao.updateUserRequest(dto);
    }

    public int deleteUserRequest(Long id) {
        return dao.deleteUserRequest(id);
    }

    // ID로 요청 조회
    public UserRequestDto getUserRequestById(Long id) {
        return dao.selectUserRequestById(id);
    }

    // 가입요청 승인 처리
    @Transactional
    public void approveUserRequest(Long requestId) {
        // 상태를 승인으로 변경
        dao.processUserRequest(requestId, GroupSubscriptionStatus.APPROVED.name());

        // 가입 요청 정보 조회
        UserRequestDto request = dao.selectUserRequestById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("가입 요청을 찾을 수 없습니다.");
        }
        // 멤버십에 추가
        studyMembershipService.addMembership(request.getUserId(), request.getGroupId());
    }

    // 가입요청 거절 처리
    public void rejectUserRequest(Long requestId) {
        // 상태를 거절로 변경
        dao.processUserRequest(requestId, GroupSubscriptionStatus.REJECTED.name());
    }

}
