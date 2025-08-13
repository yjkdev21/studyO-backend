// src/main/java/com/ex/tjspring/admin/service/AdminService.java
package com.ex.tjspring.admin.service;

import com.ex.tjspring.admin.model.UserModel;
import com.ex.tjspring.admin.model.StudyGroupModel;
import java.util.List;

public interface AdminService {
    List<UserModel> getAllUsers();
    List<UserModel> searchUsers(String searchKeyword);
    UserModel getUserDetails(String userId);
    List<StudyGroupModel> getUserStudyGroups(String userId);
    void deleteUser(String userId);


    // 스터디 그룹 관련 메서드 추가
    List<StudyGroupModel> getAllStudyGroups();
    List<StudyGroupModel> searchStudyGroups(String searchKeyword);
    void deleteStudyGroup(Long groupId);
}