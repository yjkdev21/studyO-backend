package com.ex.tjspring.study.service;

import com.ex.tjspring.study.dao.IUserRequestDao;
import com.ex.tjspring.study.dto.GroupDto;
import com.ex.tjspring.study.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRequestService {

    private final IUserRequestDao dao;


    // ## 유저가 해당 스터디그룹에 가입신청한 적이 있는 지....
    public boolean checkUserStatusForApplication(Long groupId, Long userId, Long studyPostId) {
        return dao.checkUserStatusForApplication(groupId, userId, studyPostId) < 1;
    }

    // ## 가입신청할 스터디 그룹정보 가져오기..
    public GroupDto selectStudyGroupFindByGroupId(Long groupId) {
        return dao.selectStudyGroupFindByGroupId(groupId);
    }

    // ## 가입 신청 ##
    public int insertUserRequest(UserRequestDto dto) {
        return dao.insertUserRequest(dto);
    }









    // GroupId 로 신청목록 가져오기
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
}
