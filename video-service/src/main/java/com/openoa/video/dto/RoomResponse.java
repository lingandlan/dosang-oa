package com.openoa.video.dto;

import lombok.Data;
import java.util.List;

@Data
public class RoomResponse {
    private String roomId;
    private String roomName;
    private String roomType;
    private Long creatorId;
    private String creatorName;
    private Integer maxParticipants;
    private Boolean enableRecording;
    private Boolean enableScreenShare;
    private String status;
    private List<ParticipantInfo> participants;
    private String webrtcConfig;
    
    @Data
    public static class ParticipantInfo {
        private Long userId;
        private String userName;
        private String userAvatar;
        private String role;
        private Boolean isMuted;
        private Boolean isVideoOff;
        private Boolean isScreenSharing;
    }
}
