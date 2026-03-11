package com.openoa.approval.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.openoa.approval.BaseTest;
import com.openoa.approval.context.ApprovalContext;
import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.entity.ApprovalNode;
import com.openoa.approval.entity.ApprovalRecord;
import com.openoa.approval.enums.ApprovalStatus;
import com.openoa.approval.mapper.ApprovalInstanceMapper;
import com.openoa.approval.mapper.ApprovalNodeMapper;
import com.openoa.approval.mapper.ApprovalRecordMapper;
import com.openoa.approval.service.ApprovalNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApprovalEngineTest extends BaseTest {

    @Mock
    private ApprovalInstanceMapper instanceMapper;

    @Mock
    private ApprovalNodeMapper nodeMapper;

    @Mock
    private ApprovalRecordMapper recordMapper;

    @Mock
    private ApprovalNotificationService notificationService;

    @InjectMocks
    private ApprovalEngine approvalEngine;

    private ApprovalInstance testInstance;
    private ApprovalNode node1;
    private ApprovalNode node2;
    private ApprovalNode node3;
    private List<ApprovalNode> testNodes;

    @BeforeEach
    void setUp() {
        testInstance = new ApprovalInstance();
        testInstance.setId(1L);
        testInstance.setTypeId(1L);
        testInstance.setTemplateId(1L);
        testInstance.setUserId(1L);
        testInstance.setTitle("测试申请");
        testInstance.setContent("测试内容");
        testInstance.setCreateTime(LocalDateTime.now());
        testInstance.setUpdateTime(LocalDateTime.now());

        node1 = new ApprovalNode();
        node1.setId(1L);
        node1.setTemplateId(1L);
        node1.setName("一级审批");
        node1.setSequence(1);
        node1.setType("APPROVE");
        node1.setApproverType("USER");
        node1.setApproverConfig("2");
        node1.setRequired(true);

        node2 = new ApprovalNode();
        node2.setId(2L);
        node2.setTemplateId(1L);
        node2.setName("二级审批");
        node2.setSequence(2);
        node2.setType("APPROVE");
        node2.setApproverType("USER");
        node2.setApproverConfig("3");
        node2.setRequired(true);

        node3 = new ApprovalNode();
        node3.setId(3L);
        node3.setTemplateId(1L);
        node3.setName("三级审批");
        node3.setSequence(3);
        node3.setType("APPROVE");
        node3.setApproverType("USER");
        node3.setApproverConfig("4");
        node3.setRequired(true);

        testNodes = Arrays.asList(node1, node2, node3);
    }

    @Test
    void startProcess_withValidTemplate_shouldSetFirstNode() {
        when(nodeMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(testNodes);
        when(instanceMapper.insert(any(ApprovalInstance.class))).thenReturn(1);

        ApprovalInstance result = approvalEngine.startProcess(testInstance);

        assertNotNull(result);
        assertEquals(ApprovalStatus.PENDING.name(), result.getStatus());
        assertEquals(3, result.getTotalLevels());
        assertEquals(0, result.getCurrentLevel());
        assertEquals(node1.getId(), result.getCurrentNodeId());
        assertEquals(1L, result.getCurrentApproverId());
        verify(instanceMapper, times(1)).insert(any(ApprovalInstance.class));
        verify(notificationService, times(1)).notifySubmit(any(ApprovalInstance.class));
    }

    @Test
    void startProcess_withEmptyNodes_shouldStillCreateInstance() {
        when(nodeMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList());
        when(instanceMapper.insert(any(ApprovalInstance.class))).thenReturn(1);

        ApprovalInstance result = approvalEngine.startProcess(testInstance);

        assertNotNull(result);
        assertEquals(ApprovalStatus.PENDING.name(), result.getStatus());
        assertEquals(0, result.getTotalLevels());
        assertNull(result.getCurrentNodeId());
        verify(instanceMapper, times(1)).insert(any(ApprovalInstance.class));
    }

    @Test
    void approve_withMoreNodes_shouldMoveToNextNode() {
        testInstance.setCurrentNodeId(node1.getId());
        testInstance.setStatus(ApprovalStatus.PENDING.name());

        when(instanceMapper.selectById(1L)).thenReturn(testInstance);
        when(nodeMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(testNodes);
        when(instanceMapper.updateById(any(ApprovalInstance.class))).thenReturn(1);
        when(recordMapper.insert(any(ApprovalRecord.class))).thenReturn(1);

        ApprovalContext context = approvalEngine.createContext(1L, 2L, "审批人", "同意");
        approvalEngine.approve(context);

        assertEquals(node2.getId(), testInstance.getCurrentNodeId());
        assertEquals(1, testInstance.getCurrentLevel());
        verify(instanceMapper, times(1)).updateById(any(ApprovalInstance.class));
        verify(notificationService, times(1)).notifyNextApprover(any(ApprovalInstance.class));
    }

    @Test
    void approve_withLastNode_shouldApprove() {
        testInstance.setCurrentNodeId(node3.getId());
        testInstance.setStatus(ApprovalStatus.PENDING.name());

        when(instanceMapper.selectById(1L)).thenReturn(testInstance);
        when(nodeMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(testNodes);
        when(instanceMapper.updateById(any(ApprovalInstance.class))).thenReturn(1);
        when(recordMapper.insert(any(ApprovalRecord.class))).thenReturn(1);

        ApprovalContext context = approvalEngine.createContext(1L, 4L, "审批人", "同意");
        approvalEngine.approve(context);

        assertEquals(ApprovalStatus.APPROVED.name(), testInstance.getStatus());
        assertEquals(3, testInstance.getCurrentLevel());
        verify(instanceMapper, times(1)).updateById(any(ApprovalInstance.class));
        verify(notificationService, times(1)).notifyApprove(any(ApprovalInstance.class), eq(4L));
    }

    @Test
    void reject_shouldRejectInstance() {
        testInstance.setCurrentNodeId(node1.getId());
        testInstance.setStatus(ApprovalStatus.PENDING.name());

        when(instanceMapper.selectById(1L)).thenReturn(testInstance);
        when(nodeMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(testNodes);
        when(instanceMapper.updateById(any(ApprovalInstance.class))).thenReturn(1);
        when(recordMapper.insert(any(ApprovalRecord.class))).thenReturn(1);

        ApprovalContext context = approvalEngine.createContext(1L, 2L, "审批人", "拒绝");
        approvalEngine.reject(context);

        assertEquals(ApprovalStatus.REJECTED.name(), testInstance.getStatus());
        verify(instanceMapper, times(1)).updateById(any(ApprovalInstance.class));
        verify(notificationService, times(1)).notifyReject(any(ApprovalInstance.class), eq(2L));
    }

    @Test
    void returnProcess_shouldReturnInstance() {
        testInstance.setCurrentNodeId(node1.getId());
        testInstance.setStatus(ApprovalStatus.PENDING.name());

        when(instanceMapper.selectById(1L)).thenReturn(testInstance);
        when(nodeMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(testNodes);
        when(instanceMapper.updateById(any(ApprovalInstance.class))).thenReturn(1);
        when(recordMapper.insert(any(ApprovalRecord.class))).thenReturn(1);

        ApprovalContext context = approvalEngine.createContext(1L, 2L, "审批人", "退回");
        approvalEngine.returnProcess(context);

        assertEquals(ApprovalStatus.RETURNED.name(), testInstance.getStatus());
        verify(instanceMapper, times(1)).updateById(any(ApprovalInstance.class));
        verify(notificationService, times(1)).notifyReturn(any(ApprovalInstance.class), eq(2L));
    }

    @Test
    void withdraw_shouldCancelInstance() {
        testInstance.setStatus(ApprovalStatus.PENDING.name());

        when(instanceMapper.updateById(any(ApprovalInstance.class))).thenReturn(1);

        approvalEngine.withdraw(testInstance);

        assertEquals(ApprovalStatus.CANCELLED.name(), testInstance.getStatus());
        verify(instanceMapper, times(1)).updateById(any(ApprovalInstance.class));
    }

    @Test
    void createContext_shouldPopulateAllFields() {
        testInstance.setCurrentNodeId(node1.getId());

        when(instanceMapper.selectById(1L)).thenReturn(testInstance);
        when(nodeMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(testNodes);

        ApprovalContext context = approvalEngine.createContext(1L, 2L, "审批人", "测试意见");

        assertNotNull(context);
        assertEquals(testInstance, context.getInstance());
        assertEquals(testNodes, context.getNodes());
        assertEquals(node1, context.getCurrentNode());
        assertEquals(2L, context.getOperatorId());
        assertEquals("审批人", context.getOperatorName());
        assertEquals("测试意见", context.getComment());
    }
}
