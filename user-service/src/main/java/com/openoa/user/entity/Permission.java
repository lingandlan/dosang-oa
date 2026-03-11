package com.openoa.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_permission")
public class Permission {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("permission_name")
    private String permissionName;

    @TableField("permission_code")
    private String permissionCode;

    @TableField("resource_type")
    private String resourceType;

    @TableField("resource_url")
    private String resourceUrl;

    @TableField("parent_id")
    private Long parentId;

    private Integer sortOrder;

    private String description;

    @TableField(exist = false)
    private List<Permission> children;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
