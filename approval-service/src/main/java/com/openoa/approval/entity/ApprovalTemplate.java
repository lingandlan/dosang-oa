package com.openoa.approval.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("approval_template")
public class ApprovalTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String code;
    
    private String description;
    
    private String formConfig;
    
    @TableField("is_active")
    private Boolean isActive;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
