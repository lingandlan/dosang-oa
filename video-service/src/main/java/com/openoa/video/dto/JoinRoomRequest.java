package com.openoa.video.dto;

import lombok.Data;

@Data
public class JoinRoomRequest {
    private String roomId;
    private Long userId;
    private String userName;
    private String password;
}
