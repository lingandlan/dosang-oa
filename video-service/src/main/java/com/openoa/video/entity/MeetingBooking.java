package com.openoa.video.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("meeting_booking")
public class MeetingBooking {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("room_id")
    private Long roomId;
    private String roomName;
    @TableField("user_id")
    private Long userId;
    private String userName;
    @TableField("department_id")
    private Long departmentId;
    private String title;
    private String description;
    @TableField("start_time")
    private LocalDateTime startTime;
    @TableField("end_time")
    private LocalDateTime endTime;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}