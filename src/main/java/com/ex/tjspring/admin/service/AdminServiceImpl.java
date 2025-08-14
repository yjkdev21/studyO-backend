// src/main/java/com/ex/tjspring/admin/service/AdminServiceImpl.java
package com.ex.tjspring.admin.service;

import com.ex.tjspring.admin.mapper.AdminMapper;
import com.ex.tjspring.admin.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ex.tjspring.admin.model.StudyGroupModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminMapper adminMapper;

    @Override
    public List<UserModel> getAllUsers() {
        return adminMapper.findAllUsers();
    }

    @Override
    public List<UserModel> searchUsers(String searchKeyword) {
        return adminMapper.searchUsers(searchKeyword);
    }
    @Override
    public UserModel getUserDetails(String userId) {
        return adminMapper.findUserByUserId(userId);
    }

    @Override
    public List<StudyGroupModel> getUserStudyGroups(String userId) {
        return adminMapper.findUserStudyGroups(userId);
    }

    @Override
    public void deleteUser(String userId) {
        adminMapper.deleteUser(userId);
    }


    @Override
    public List<StudyGroupModel> getAllStudyGroups() {
        return adminMapper.findAllStudyGroups();
    }

    @Override
    public List<StudyGroupModel> searchStudyGroups(String searchKeyword) {
        return adminMapper.searchStudyGroups(searchKeyword);
    }

    @Override
    public void deleteStudyGroup(Long groupId) {
        adminMapper.deleteStudyGroup(groupId);
    }
}