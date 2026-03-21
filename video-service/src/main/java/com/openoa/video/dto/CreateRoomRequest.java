package com.openoa.video.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateRoomRequest {
    private String roomName;
    private String roomType;
    private Long departmentId;
    private String password;
    private Integer maxParticipants;
    private Boolean enableRecording;
    private Boolean enableScreenShare;
    private String description;
    private String title;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;
    
    // 需要从Token或参数获取
    private Long creatorId;
    private String creatorName;
}
