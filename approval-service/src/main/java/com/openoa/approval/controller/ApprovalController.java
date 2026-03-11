package com.openoa.approval.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.entity.ApprovalType;
import com.openoa.approval.mapper.ApprovalInstanceMapper;
import com.openoa.approval.mapper.ApprovalTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/approvals")
public class ApprovalController {
    
    @Autowired
    private ApprovalInstanceMapper instanceMapper;
    
    @Autowired
    private ApprovalTypeMapper typeMapper;
    
    // 获取审批类型列表
    @GetMapping("/types")
    public Map<String, Object> listTypes() {
        List<ApprovalType> types = typeMapper.selectList(null);
        return Map.of("code", 200, "message", "success", "data", types);
    }
    
    // 获取审批列表
    @GetMapping
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status) {
        Page<ApprovalInstance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ApprovalInstance> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ApprovalInstance::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(ApprovalInstance::getStatus, status);
        }
        wrapper.orderByDesc(ApprovalInstance::getCreateTime);
        Page<ApprovalInstance> result = instanceMapper.selectPage(page, wrapper);
        return Map.of("code", 200, "message", "success", "data", result);
    }
    
    // 提交审批
    @PostMapping
    public Map<String, Object> create(@RequestBody ApprovalInstance instance) {
        instance.setStatus("PENDING");
        instanceMapper.insert(instance);
        return Map.of("code", 200, "message", "提交成功", "data", instance);
    }
    
    // 审批操作 (通过/拒绝)
    @PutMapping("/{id}")
    public Map<String, Object> approve(
            @PathVariable Long id,
            @RequestParam String action,
            @RequestParam(required = false) Long approverId,
            @RequestParam(required = false) String comment) {
        ApprovalInstance instance = instanceMapper.selectById(id);
        if (instance == null) {
            return Map.of("code", 404, "message", "审批不存在");
        }
        
        if ("APPROVE".equals(action)) {
            instance.setStatus("APPROVED");
        } else if ("REJECT".equals(action)) {
            instance.setStatus("REJECTED");
        }
        instance.setCurrentApproverId(approverId);
        instanceMapper.updateById(instance);
        
        return Map.of("code", 200, "message", "操作成功");
    }
    
    // 获取审批详情
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        ApprovalInstance instance = instanceMapper.selectById(id);
        return Map.of("code", 200, "message", "success", "data", instance);
    }
}
