package com.openoa.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("attendance_record")
public class AttendanceRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("check_type")
    private String checkType;
    
    @TableField("check_time")
    private LocalDateTime checkTime;
    
    private String location;
    
    private String device;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
