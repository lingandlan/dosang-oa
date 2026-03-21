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
    
    // 别名属性（兼容旧代码）
    public String getTemplateName() {
        return this.name;
    }
    
    public void setTemplateName(String templateName) {
        this.name = templateName;
    }
    
    public String getTemplateCode() {
        return this.code;
    }
    
    public void setTemplateCode(String templateCode) {
        this.code = templateCode;
    }
    
    public Long getTypeId() {
        return null; // 模板不再关联类型
    }
    
    public void setTypeId(Long typeId) {
        // 模板不再关联类型，无操作
    }
}
