package com.openoa.approval.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("approval_history")
public class ApprovalHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("instance_id")
    private Long instanceId;
    
    @TableField("approver_id")
    private Long approverId;
    
    private String action;
    
    private String comment;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
