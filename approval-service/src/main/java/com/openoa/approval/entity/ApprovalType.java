package com.openoa.approval.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("approval_type")
public class ApprovalType {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private String icon;
    private String config;
    private LocalDateTime createTime;
}
