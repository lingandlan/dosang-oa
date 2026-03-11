package com.openoa.approval.service;

import com.openoa.approval.client.NoticeClient;
import com.openoa.approval.entity.ApprovalInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ApprovalNotificationService {
    
    @Autowired
    private NoticeClient noticeClient;
    
    public void notifySubmit(ApprovalInstance instance) {
        try {
            Map<String, Object> notice = new HashMap<>();
            notice.put("title", "新的审批申请");
            notice.put("content", "您有一条新的审批申请需要处理: " + instance.getTitle());
            notice.put("type", "APPROVAL");
            notice.put("userId", instance.getCurrentApproverId());
            notice.put("relatedId", instance.getId());
            
            noticeClient.createNotice(notice);
            log.info("发送审批提交通知成功: instanceId={}", instance.getId());
        } catch (Exception e) {
            log.error("发送审批提交通知失败: instanceId={}", instance.getId(), e);
        }
    }
    
    public void notifyApprove(ApprovalInstance instance, Long approverId) {
        try {
            Map<String, Object> notice = new HashMap<>();
            notice.put("title", "审批已通过");
            notice.put("content", "您的审批申请已通过: " + instance.getTitle());
            notice.put("type", "APPROVAL");
            notice.put("userId", instance.getUserId());
            notice.put("relatedId", instance.getId());
            
            noticeClient.createNotice(notice);
            log.info("发送审批通过通知成功: instanceId={}", instance.getId());
        } catch (Exception e) {
            log.error("发送审批通过通知失败: instanceId={}", instance.getId(), e);
        }
    }
    
    public void notifyReject(ApprovalInstance instance, Long approverId) {
        try {
            Map<String, Object> notice = new HashMap<>();
            notice.put("title", "审批已拒绝");
            notice.put("content", "您的审批申请已被拒绝: " + instance.getTitle());
            notice.put("type", "APPROVAL");
            notice.put("userId", instance.getUserId());
            notice.put("relatedId", instance.getId());
            
            noticeClient.createNotice(notice);
            log.info("发送审批拒绝通知成功: instanceId={}", instance.getId());
        } catch (Exception e) {
            log.error("发送审批拒绝通知失败: instanceId={}", instance.getId(), e);
        }
    }
    
    public void notifyReturn(ApprovalInstance instance, Long approverId) {
        try {
            Map<String, Object> notice = new HashMap<>();
            notice.put("title", "审批已退回");
            notice.put("content", "您的审批申请已被退回: " + instance.getTitle());
            notice.put("type", "APPROVAL");
            notice.put("userId", instance.getUserId());
            notice.put("relatedId", instance.getId());
            
            noticeClient.createNotice(notice);
            log.info("发送审批退回通知成功: instanceId={}", instance.getId());
        } catch (Exception e) {
            log.error("发送审批退回通知失败: instanceId={}", instance.getId(), e);
        }
    }
    
    public void notifyNextApprover(ApprovalInstance instance) {
        try {
            Map<String, Object> notice = new HashMap<>();
            notice.put("title", "待审批提醒");
            notice.put("content", "您有一条审批申请需要处理: " + instance.getTitle());
            notice.put("type", "APPROVAL");
            notice.put("userId", instance.getCurrentApproverId());
            notice.put("relatedId", instance.getId());
            
            noticeClient.createNotice(notice);
            log.info("发送下一级审批人通知成功: instanceId={}", instance.getId());
        } catch (Exception e) {
            log.error("发送下一级审批人通知失败: instanceId={}", instance.getId(), e);
        }
    }
}
