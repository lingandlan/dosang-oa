package com.openoa.approval.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.openoa.approval.context.ApprovalContext;
import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.entity.ApprovalNode;
import com.openoa.approval.entity.ApprovalRecord;
import com.openoa.approval.enums.ApprovalAction;
import com.openoa.approval.enums.ApprovalStatus;
import com.openoa.approval.mapper.ApprovalInstanceMapper;
import com.openoa.approval.mapper.ApprovalNodeMapper;
import com.openoa.approval.mapper.ApprovalRecordMapper;
import com.openoa.approval.service.ApprovalNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ApprovalEngine {
    
    @Autowired
    private ApprovalInstanceMapper instanceMapper;
    
    @Autowired
    private ApprovalNodeMapper nodeMapper;
    
    @Autowired
    private ApprovalRecordMapper recordMapper;
    
    @Autowired
    private ApprovalNotificationService notificationService;
    
    public ApprovalInstance startProcess(ApprovalInstance instance) {
        instance.setStatus(ApprovalStatus.PENDING.name());
        
        List<ApprovalNode> nodes = getNodesByTemplateId(instance.getTemplateId());
        instance.setTotalLevels(nodes.size());
        instance.setCurrentLevel(0);
        
        if (!nodes.isEmpty()) {
            ApprovalNode firstNode = nodes.get(0);
            instance.setCurrentNodeId(firstNode.getId());
            instance.setCurrentApproverId(getApproverId(firstNode));
        }
        
        instanceMapper.insert(instance);
        notificationService.notifySubmit(instance);
        return instance;
    }
    
    public void approve(ApprovalContext context) {
        ApprovalInstance instance = context.getInstance();
        ApprovalNode currentNode = context.getCurrentNode();
        
        recordApproval(context, ApprovalAction.APPROVE);
        
        List<ApprovalNode> nodes = context.getNodes();
        int currentIndex = nodes.indexOf(currentNode);
        
        if (currentIndex >= 0 && currentIndex < nodes.size() - 1) {
            ApprovalNode nextNode = nodes.get(currentIndex + 1);
            instance.setCurrentNodeId(nextNode.getId());
            instance.setCurrentApproverId(getApproverId(nextNode));
            instance.setCurrentLevel(currentIndex + 1);
            instanceMapper.updateById(instance);
            notificationService.notifyNextApprover(instance);
        } else {
            instance.setStatus(ApprovalStatus.APPROVED.name());
            instance.setCurrentLevel(instance.getTotalLevels());
            instanceMapper.updateById(instance);
            notificationService.notifyApprove(instance, context.getOperatorId());
        }
    }
    
    public void reject(ApprovalContext context) {
        ApprovalInstance instance = context.getInstance();
        recordApproval(context, ApprovalAction.REJECT);
        instance.setStatus(ApprovalStatus.REJECTED.name());
        instanceMapper.updateById(instance);
        notificationService.notifyReject(instance, context.getOperatorId());
    }
    
    public void returnProcess(ApprovalContext context) {
        ApprovalInstance instance = context.getInstance();
        recordApproval(context, ApprovalAction.RETURN);
        instance.setStatus(ApprovalStatus.RETURNED.name());
        instanceMapper.updateById(instance);
        notificationService.notifyReturn(instance, context.getOperatorId());
    }
    
    public void withdraw(ApprovalInstance instance) {
        instance.setStatus(ApprovalStatus.CANCELLED.name());
        instanceMapper.updateById(instance);
    }
    
    public ApprovalContext createContext(Long instanceId, Long operatorId, String operatorName, String comment) {
        ApprovalContext context = new ApprovalContext();
        
        ApprovalInstance instance = instanceMapper.selectById(instanceId);
        List<ApprovalNode> nodes = getNodesByTemplateId(instance.getTemplateId());
        ApprovalNode currentNode = findNodeById(nodes, instance.getCurrentNodeId());
        
        context.setInstance(instance);
        context.setNodes(nodes);
        context.setCurrentNode(currentNode);
        context.setOperatorId(operatorId);
        context.setOperatorName(operatorName);
        context.setComment(comment);
        
        return context;
    }
    
    private void recordApproval(ApprovalContext context, ApprovalAction action) {
        ApprovalRecord record = new ApprovalRecord();
        record.setInstanceId(context.getInstance().getId());
        record.setNodeId(context.getCurrentNode().getId());
        record.setApproverId(context.getOperatorId());
        record.setApproverName(context.getOperatorName());
        record.setAction(action.name());
        record.setComment(context.getComment());
        recordMapper.insert(record);
    }
    
    private List<ApprovalNode> getNodesByTemplateId(Long templateId) {
        LambdaQueryWrapper<ApprovalNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalNode::getTemplateId, templateId);
        wrapper.orderByAsc(ApprovalNode::getSequence);
        return nodeMapper.selectList(wrapper);
    }
    
    private ApprovalNode findNodeById(List<ApprovalNode> nodes, Long nodeId) {
        return nodes.stream()
            .filter(node -> node.getId().equals(nodeId))
            .findFirst()
            .orElse(null);
    }
    
    private Long getApproverId(ApprovalNode node) {
        return 1L;
    }
}
