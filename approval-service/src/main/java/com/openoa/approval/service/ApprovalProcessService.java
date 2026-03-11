package com.openoa.approval.service;

import com.openoa.approval.context.ApprovalContext;
import com.openoa.approval.engine.ApprovalEngine;
import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.entity.ApprovalNode;
import com.openoa.approval.enums.ApprovalAction;
import com.openoa.approval.mapper.ApprovalInstanceMapper;
import com.openoa.approval.mapper.ApprovalNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalProcessService {
    
    @Autowired
    private ApprovalEngine approvalEngine;
    
    @Autowired
    private ApprovalInstanceMapper instanceMapper;
    
    @Autowired
    private ApprovalNodeMapper nodeMapper;
    
    public ApprovalInstance startApproval(ApprovalInstance instance) {
        return approvalEngine.startProcess(instance);
    }
    
    public void approve(Long instanceId, Long operatorId, String operatorName, String comment) {
        ApprovalContext context = approvalEngine.createContext(instanceId, operatorId, operatorName, comment);
        approvalEngine.approve(context);
    }
    
    public void reject(Long instanceId, Long operatorId, String operatorName, String comment) {
        ApprovalContext context = approvalEngine.createContext(instanceId, operatorId, operatorName, comment);
        approvalEngine.reject(context);
    }
    
    public void returnProcess(Long instanceId, Long operatorId, String operatorName, String comment) {
        ApprovalContext context = approvalEngine.createContext(instanceId, operatorId, operatorName, comment);
        approvalEngine.returnProcess(context);
    }
    
    public void withdraw(Long instanceId) {
        ApprovalInstance instance = instanceMapper.selectById(instanceId);
        approvalEngine.withdraw(instance);
    }
    
    public ApprovalInstance getInstance(Long id) {
        return instanceMapper.selectById(id);
    }
    
    public List<ApprovalNode> getNodesByTemplateId(Long templateId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ApprovalNode> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ApprovalNode::getTemplateId, templateId);
        wrapper.orderByAsc(ApprovalNode::getSequence);
        return nodeMapper.selectList(wrapper);
    }
}
