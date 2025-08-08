package com.ex.tjspring.study.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

@Getter
@Setter
@ToString
public class UserRequestDto {

    private Long id;

    private Long groupId;

    @NotNull
    private Long userId;

    @NotNull
    private Long studyPostId;

    private Date requestedAt;

    @Pattern(regexp = "PENDING|APPROVED|REJECTED|CANCELLED", message = "Invalid application status")
    private String applicationStatus;

    @Size(max = 500)
    private String applicationMessage;

    private Date processedAt;

    private Long processedBy;

    @Size(max = 500)
    private String rejectionReason;
}
