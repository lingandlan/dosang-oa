package com.openoa.websocket.dto;

import lombok.Data;

@Data
public class SignalingMessage {
    private String type;
    private String roomId;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private Object payload;
    private String sdp;
    private String candidate;
    private Integer sdpMid;
    private Integer sdpMLineIndex;
}
