package com.openoa.video.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("video_participant")
public class VideoParticipant {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("room_id")
    private Long roomId;
    private String roomIdStr;
    @TableField("user_id")
    private Long userId;
    private String userName;
    @TableField("join_time")
    private LocalDateTime joinTime;
    @TableField("leave_time")
    private LocalDateTime leaveTime;
    private Integer duration;
    private String role;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
