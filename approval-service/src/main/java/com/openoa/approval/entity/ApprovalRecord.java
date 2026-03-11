package com.openoa.approval.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("approval_record")
public class ApprovalRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("instance_id")
    private Long instanceId;
    
    @TableField("node_id")
    private Long nodeId;
    
    @TableField("approver_id")
    private Long approverId;
    
    @TableField("approver_name")
    private String approverName;
    
    private String action;
    
    private String comment;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
