package com.openoa.video.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("room")
public class Room {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String roomId;
    private String roomName;
    @TableField("creator_id")
    private Long creatorId;
    @TableField("department_id")
    private Long departmentId;
    private String description;
    private Integer capacity;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}