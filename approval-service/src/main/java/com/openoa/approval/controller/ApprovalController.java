package com.openoa.approval.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.entity.ApprovalNode;
import com.openoa.approval.entity.ApprovalType;
import com.openoa.approval.mapper.ApprovalInstanceMapper;
import com.openoa.approval.mapper.ApprovalRecordMapper;
import com.openoa.approval.mapper.ApprovalTypeMapper;
import com.openoa.approval.service.ApprovalProcessService;
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
    
    @Autowired
    private ApprovalRecordMapper recordMapper;
    
    @Autowired
    private ApprovalProcessService processService;
    
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
        ApprovalInstance created = processService.startApproval(instance);
        return Map.of("code", 200, "message", "提交成功", "data", created);
    }
    
    // 审批操作 (通过/拒绝/退回)
    @PutMapping("/{id}")
    public Map<String, Object> approve(
            @PathVariable Long id,
            @RequestParam String action,
            @RequestParam Long approverId,
            @RequestParam String approverName,
            @RequestParam(required = false) String comment) {
        try {
            if ("APPROVE".equals(action)) {
                processService.approve(id, approverId, approverName, comment);
            } else if ("REJECT".equals(action)) {
                processService.reject(id, approverId, approverName, comment);
            } else if ("RETURN".equals(action)) {
                processService.returnProcess(id, approverId, approverName, comment);
            } else {
                return Map.of("code", 400, "message", "无效的操作类型");
            }
            return Map.of("code", 200, "message", "操作成功");
        } catch (Exception e) {
            return Map.of("code", 500, "message", "操作失败: " + e.getMessage());
        }
    }
    
    // 撤回审批
    @PutMapping("/{id}/withdraw")
    public Map<String, Object> withdraw(@PathVariable Long id) {
        try {
            processService.withdraw(id);
            return Map.of("code", 200, "message", "撤回成功");
        } catch (Exception e) {
            return Map.of("code", 500, "message", "撤回失败: " + e.getMessage());
        }
    }
    
    // 获取审批详情
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        ApprovalInstance instance = processService.getInstance(id);
        if (instance == null) {
            return Map.of("code", 404, "message", "审批不存在");
        }
        
        List<ApprovalNode> nodes = processService.getNodesByTemplateId(instance.getTemplateId());
        
        return Map.of("code", 200, "message", "success", "data", Map.of(
            "instance", instance,
            "nodes", nodes
        ));
    }
    
    // 获取待我审批的列表
    @GetMapping("/pending")
    public Map<String, Object> listPending(
            @RequestParam Long approverId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ApprovalInstance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ApprovalInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalInstance::getCurrentApproverId, approverId);
        wrapper.eq(ApprovalInstance::getStatus, "PENDING");
        wrapper.orderByDesc(ApprovalInstance::getCreateTime);
        Page<ApprovalInstance> result = instanceMapper.selectPage(page, wrapper);
        return Map.of("code", 200, "message", "success", "data", result);
    }
}
