package com.openoa.notice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("notice")
public class Notice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    @TableField("publisher_id")
    private Long publisherId;
    @TableField("publish_time")
    private LocalDateTime publishTime;
    private String status;
    private String category;
    private Integer pinned;
    private String scope;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
