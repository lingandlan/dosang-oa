package com.openoa.approval.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.mapper.ApprovalInstanceMapper;
import org.springframework.stereotype.Service;

@Service
public class ApprovalService extends ServiceImpl<ApprovalInstanceMapper, ApprovalInstance> {

    public Page<ApprovalInstance> pageList(Integer pageNum, Integer pageSize, Long userId, String status) {
        Page<ApprovalInstance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ApprovalInstance> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ApprovalInstance::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(ApprovalInstance::getStatus, status);
        }
        wrapper.orderByDesc(ApprovalInstance::getCreateTime);
        return page(page, wrapper);
    }

    public ApprovalInstance approve(Long id, String action, Long approverId, String comment) {
        ApprovalInstance instance = getById(id);
        if (instance == null) {
            return null;
        }

        if ("APPROVE".equals(action)) {
            instance.setStatus("APPROVED");
        } else if ("REJECT".equals(action)) {
            instance.setStatus("REJECTED");
        }
        instance.setCurrentApproverId(approverId);
        updateById(instance);
        return instance;
    }

    public ApprovalInstance submit(ApprovalInstance instance) {
        instance.setStatus("PENDING");
        save(instance);
        return instance;
    }

    public Page<ApprovalInstance> listByApprover(Long approverId, Integer pageNum, Integer pageSize) {
        Page<ApprovalInstance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ApprovalInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalInstance::getCurrentApproverId, approverId);
        wrapper.orderByDesc(ApprovalInstance::getCreateTime);
        return page(page, wrapper);
    }
}
