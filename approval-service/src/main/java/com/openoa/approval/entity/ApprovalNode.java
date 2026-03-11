package com.openoa.approval.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("approval_node")
public class ApprovalNode {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("template_id")
    private Long templateId;
    
    private String name;
    
    private Integer sequence;
    
    private String type;
    
    private String approverType;
    
    @TableField("approver_config")
    private String approverConfig;
    
    private Boolean required;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
