package com.openoa.approval.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.approval.common.Result;
import com.openoa.approval.entity.ApprovalHistory;
import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.entity.ApprovalType;
import com.openoa.approval.mapper.ApprovalHistoryMapper;
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

    @Autowired
    private ApprovalHistoryMapper historyMapper;
    
    @GetMapping("/types")
    public Result<List<ApprovalType>> listTypes() {
        List<ApprovalType> types = typeMapper.selectList(null);
        return Result.success(types);
    }

    @GetMapping("/types/{id}")
    public Result<ApprovalType> getTypeById(@PathVariable Long id) {
        ApprovalType type = typeMapper.selectById(id);
        if (type == null) {
            return Result.error("审批类型不存在");
        }
        return Result.success(type);
    }

    @PostMapping("/types")
    public Result<ApprovalType> createType(@RequestBody ApprovalType type) {
        typeMapper.insert(type);
        return Result.success("创建成功", type);
    }

    @PutMapping("/types/{id}")
    public Result<ApprovalType> updateType(@PathVariable Long id, @RequestBody ApprovalType type) {
        ApprovalType existing = typeMapper.selectById(id);
        if (existing == null) {
            return Result.error("审批类型不存在");
        }
        type.setId(id);
        typeMapper.updateById(type);
        return Result.success("更新成功", type);
    }

    @DeleteMapping("/types/{id}")
    public Result<Void> deleteType(@PathVariable Long id) {
        int rows = typeMapper.deleteById(id);
        if (rows == 0) {
            return Result.error("审批类型不存在");
        }
        return Result.success("删除成功", null);
    }
    
    @GetMapping
    public Result<Page<ApprovalInstance>> list(
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
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<ApprovalInstance> getById(@PathVariable Long id) {
        ApprovalInstance instance = instanceMapper.selectById(id);
        if (instance == null) {
            return Result.error("审批不存在");
        }
        return Result.success(instance);
    }

    @PostMapping
    public Result<ApprovalInstance> create(@RequestBody ApprovalInstance instance) {
        instance.setStatus("PENDING");
        instanceMapper.insert(instance);
        return Result.success("提交成功", instance);
    }

    @PutMapping("/{id}")
    public Result<ApprovalInstance> approve(
            @PathVariable Long id,
            @RequestParam String action,
            @RequestParam(required = false) Long approverId,
            @RequestParam(required = false) String comment) {
        ApprovalInstance instance = instanceMapper.selectById(id);
        if (instance == null) {
            return Result.error("审批不存在");
        }

        if ("APPROVE".equals(action)) {
            instance.setStatus("APPROVED");
        } else if ("REJECT".equals(action)) {
            instance.setStatus("REJECTED");
        }
        instance.setCurrentApproverId(approverId);
        instanceMapper.updateById(instance);

        ApprovalHistory history = new ApprovalHistory();
        history.setInstanceId(id);
        history.setApproverId(approverId);
        history.setAction(action);
        history.setComment(comment);
        historyMapper.insert(history);

        return Result.success("操作成功", instance);
    }

    @PutMapping("/{id}/status")
    public Result<ApprovalInstance> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ApprovalInstance instance = instanceMapper.selectById(id);
        if (instance == null) {
            return Result.error("审批不存在");
        }
        instance.setStatus(status);
        instanceMapper.updateById(instance);
        return Result.success("状态更新成功", instance);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        int rows = instanceMapper.deleteById(id);
        if (rows == 0) {
            return Result.error("审批不存在");
        }
        return Result.success("删除成功", null);
    }

    @GetMapping("/{instanceId}/history")
    public Result<List<ApprovalHistory>> getHistory(@PathVariable Long instanceId) {
        LambdaQueryWrapper<ApprovalHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalHistory::getInstanceId, instanceId);
        wrapper.orderByDesc(ApprovalHistory::getCreateTime);
        List<ApprovalHistory> history = historyMapper.selectList(wrapper);
        return Result.success(history);
    }

    @GetMapping("/pending/{approverId}")
    public Result<List<ApprovalInstance>> getPendingApprovals(@PathVariable Long approverId) {
        LambdaQueryWrapper<ApprovalInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalInstance::getCurrentApproverId, approverId);
        wrapper.eq(ApprovalInstance::getStatus, "PENDING");
        wrapper.orderByDesc(ApprovalInstance::getCreateTime);
        List<ApprovalInstance> pending = instanceMapper.selectList(wrapper);
        return Result.success(pending);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestParam Long userId) {
        LambdaQueryWrapper<ApprovalInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalInstance::getUserId, userId);

        long total = instanceMapper.selectCount(wrapper);

        LambdaQueryWrapper<ApprovalInstance> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(ApprovalInstance::getUserId, userId);
        pendingWrapper.eq(ApprovalInstance::getStatus, "PENDING");
        long pending = instanceMapper.selectCount(pendingWrapper);

        LambdaQueryWrapper<ApprovalInstance> approvedWrapper = new LambdaQueryWrapper<>();
        approvedWrapper.eq(ApprovalInstance::getUserId, userId);
        approvedWrapper.eq(ApprovalInstance::getStatus, "APPROVED");
        long approved = instanceMapper.selectCount(approvedWrapper);

        LambdaQueryWrapper<ApprovalInstance> rejectedWrapper = new LambdaQueryWrapper<>();
        rejectedWrapper.eq(ApprovalInstance::getUserId, userId);
        rejectedWrapper.eq(ApprovalInstance::getStatus, "REJECTED");
        long rejected = instanceMapper.selectCount(rejectedWrapper);

        Map<String, Object> stats = Map.of(
                "total", total,
                "pending", pending,
                "approved", approved,
                "rejected", rejected
        );
        return Result.success(stats);
    }
}
