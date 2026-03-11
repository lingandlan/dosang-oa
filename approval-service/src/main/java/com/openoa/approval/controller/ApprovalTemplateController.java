package com.openoa.approval.controller;

import com.openoa.approval.entity.ApprovalTemplate;
import com.openoa.approval.service.ApprovalTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/approval-templates")
public class ApprovalTemplateController {
    
    @Autowired
    private ApprovalTemplateService templateService;
    
    @GetMapping
    public Map<String, Object> list() {
        List<ApprovalTemplate> templates = templateService.getAllTemplates();
        return Map.of("code", 200, "message", "success", "data", templates);
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        ApprovalTemplate template = templateService.getById(id);
        return Map.of("code", 200, "message", "success", "data", template);
    }
    
    @PostMapping
    public Map<String, Object> create(@RequestBody ApprovalTemplate template) {
        ApprovalTemplate created = templateService.createTemplate(template);
        return Map.of("code", 200, "message", "创建成功", "data", created);
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody ApprovalTemplate template) {
        template.setId(id);
        ApprovalTemplate updated = templateService.updateTemplate(template);
        return Map.of("code", 200, "message", "更新成功", "data", updated);
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return Map.of("code", 200, "message", "删除成功");
    }
}
