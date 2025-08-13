// src/main/java/com/ex/tjspring/admin/mapper/AdminMapper.java
package com.ex.tjspring.admin.mapper;

import com.ex.tjspring.admin.model.UserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {
    List<UserModel> findAllUsers();
    List<UserModel> searchUsers(@Param("searchKeyword") String searchKeyword);

}