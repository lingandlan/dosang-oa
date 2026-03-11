package com.openoa.video.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("video_room")
public class VideoRoom {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String roomId;
    private String roomName;
    @TableField("creator_id")
    private Long creatorId;
    private String status;
    @TableField("start_time")
    private LocalDateTime startTime;
    @TableField("end_time")
    private LocalDateTime endTime;
    @TableField("participant_count")
    private Integer participantCount;
    @TableField("max_participants")
    private Integer maxParticipants;
    @TableField("meeting_url")
    private String meetingUrl;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
