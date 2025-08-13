// src/main/java/com/ex/tjspring/admin/model/UserModel.java
package com.ex.tjspring.admin.model;

import lombok.Data;
import java.util.Date;

@Data
public class UserModel {
    private Long id;
    private String userId;
    private String email;
    private String nickname;
    private String isDeleted;
    private Date createdAt;
    private String globalRole;
    private String profileImage;
    private String introduction;
}