package com.ex.tjspring.study.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupDto {
    private Long groupId;
    @Size(max =20, message = "20글자 이내로 입력해주세요.")
    private String groupName;
    @Size(max =6, message = "6글자 이내로 입력해주세요.")
    private String nickname;
    private String category;
    @Min(value = 1, message = "최대 인원은 최소 1명 이상이어야 합니다.")
    private Integer maxMembers;
    private String studyMode;
    private String region;
    private String contact;
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
    private String groupIntroduction;
    private Long groupOwnerId;
    private LocalDateTime createdAt;
    private String thumbnail;
    private String thumbnailFullPath;

    // 추가된 필드: 사용자의 멤버십 상태
    private String membershipStatus;
    private String memberRole;

    // 그룹장의 닉네임 (STUDY_MEMBERSHIP 테이블에서 조회)
    private String ownerNickname;

    private String ownerProfileImage;
    private String ownerProfileImageFullPath;

}