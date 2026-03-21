package com.openoa.video.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("meeting_participant")
public class MeetingParticipant {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("room_id")
    private Long roomId;
    @TableField("booking_id")
    private Long bookingId;
    @TableField("user_id")
    private Long userId;
    @TableField("user_name")
    private String userName;
    private String role;
    private String status;
    @TableField("join_time")
    private LocalDateTime joinTime;
    @TableField("leave_time")
    private LocalDateTime leaveTime;
    private Integer duration;
    @TableField("is_muted")
    private Boolean isMuted;
    @TableField("is_video_off")
    private Boolean isVideoOff;
    @TableField("is_screen_sharing")
    private Boolean isScreenSharing;
    @TableField("user_avatar")
    private String userAvatar;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getJoinTime() { return joinTime; }
    public void setJoinTime(LocalDateTime joinTime) { this.joinTime = joinTime; }
    public LocalDateTime getLeaveTime() { return leaveTime; }
    public void setLeaveTime(LocalDateTime leaveTime) { this.leaveTime = leaveTime; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public Boolean getIsMuted() { return isMuted; }
    public void setIsMuted(Boolean isMuted) { this.isMuted = isMuted; }
    public Boolean getIsVideoOff() { return isVideoOff; }
    public void setIsVideoOff(Boolean isVideoOff) { this.isVideoOff = isVideoOff; }
    public Boolean getIsScreenSharing() { return isScreenSharing; }
    public void setIsScreenSharing(Boolean isScreenSharing) { this.isScreenSharing = isScreenSharing; }
    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
}
