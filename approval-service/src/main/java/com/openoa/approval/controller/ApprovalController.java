package com.openoa.approval.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.entity.ApprovalType;
import com.openoa.approval.mapper.ApprovalTypeMapper;
import com.openoa.approval.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private ApprovalTypeMapper typeMapper;

    @GetMapping("/types")
    public Map<String, Object> listTypes() {
        List<ApprovalType> types = typeMapper.selectList(null);
        return Map.of("code", 200, "message", "success", "data", types);
    }

    @GetMapping
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status) {
        Page<ApprovalInstance> result = approvalService.pageList(pageNum, pageSize, userId, status);
        return Map.of("code", 200, "message", "success", "data", result);
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody ApprovalInstance instance) {
        ApprovalInstance result = approvalService.submit(instance);
        return Map.of("code", 200, "message", "提交成功", "data", result);
    }

    @PutMapping("/{id}")
    public Map<String, Object> approve(
            @PathVariable Long id,
            @RequestParam String action,
            @RequestParam(required = false) Long approverId,
            @RequestParam(required = false) String comment) {
        ApprovalInstance result = approvalService.approve(id, action, approverId, comment);
        if (result == null) {
            return Map.of("code", 404, "message", "审批不存在");
        }
        return Map.of("code", 200, "message", "操作成功");
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        ApprovalInstance instance = approvalService.getById(id);
        return Map.of("code", 200, "message", "success", "data", instance);
    }

    @GetMapping("/approver/{approverId}")
    public Map<String, Object> listByApprover(
            @PathVariable Long approverId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ApprovalInstance> result = approvalService.listByApprover(approverId, pageNum, pageSize);
        return Map.of("code", 200, "message", "success", "data", result);
    }
}
