package com.openoa.approval.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("approval_instance")
public class ApprovalInstance {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("type_id")
    private Long typeId;
    
    @TableField("template_id")
    private Long templateId;
    
    @TableField("user_id")
    private Long userId;
    
    private String title;
    
    private String content;
    
    private String status;
    
    @TableField("current_node_id")
    private Long currentNodeId;
    
    @TableField("current_approver_id")
    private Long currentApproverId;
    
    private Integer currentLevel;
    
    @TableField("total_levels")
    private Integer totalLevels;
    
    private String formData;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
