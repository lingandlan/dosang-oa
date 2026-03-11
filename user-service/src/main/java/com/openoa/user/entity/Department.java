package com.openoa.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_department")
public class Department {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("dept_name")
    private String deptName;

    @TableField("dept_code")
    private String deptCode;

    private Integer sortOrder;

    @TableField("parent_id")
    private Long parentId;

    private Integer status;

    private String remark;

    @TableField(exist = false)
    private List<Department> children;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
