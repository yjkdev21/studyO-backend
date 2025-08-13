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
}