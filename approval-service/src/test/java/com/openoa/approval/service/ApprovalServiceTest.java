package com.openoa.approval.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.approval.BaseTest;
import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.mapper.ApprovalInstanceMapper;
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

class ApprovalServiceTest extends BaseTest {

    @Mock
    private ApprovalInstanceMapper instanceMapper;

    @InjectMocks
    private ApprovalService approvalService;

    private ApprovalInstance testInstance;
    private ApprovalInstance testInstance2;

    @BeforeEach
    void setUp() {
        testInstance = new ApprovalInstance();
        testInstance.setId(1L);
        testInstance.setTypeId(1L);
        testInstance.setUserId(1L);
        testInstance.setTitle("请假申请");
        testInstance.setContent("因事请假一天");
        testInstance.setStatus("PENDING");
        testInstance.setCurrentApproverId(2L);
        testInstance.setCreateTime(LocalDateTime.now());
        testInstance.setUpdateTime(LocalDateTime.now());

        testInstance2 = new ApprovalInstance();
        testInstance2.setId(2L);
        testInstance2.setTypeId(1L);
        testInstance2.setUserId(2L);
        testInstance2.setTitle("报销申请");
        testInstance2.setContent("差旅报销");
        testInstance2.setStatus("APPROVED");
        testInstance2.setCurrentApproverId(3L);
        testInstance2.setCreateTime(LocalDateTime.now());
        testInstance2.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void pageList_withAllParameters_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(instanceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<ApprovalInstance> result = approvalService.pageList(1, 10, 1L, "PENDING");

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(instanceMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageList_withOnlyUserId_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(instanceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<ApprovalInstance> result = approvalService.pageList(1, 10, 1L, null);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(instanceMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageList_withOnlyStatus_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance2));

        when(instanceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<ApprovalInstance> result = approvalService.pageList(1, 10, null, "APPROVED");

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(instanceMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageList_withNoFilters_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance, testInstance2));

        when(instanceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<ApprovalInstance> result = approvalService.pageList(1, 10, null, null);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        verify(instanceMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void approve_withApproveAction_shouldUpdateStatus() {
        when(instanceMapper.selectById(1L)).thenReturn(testInstance);
        when(instanceMapper.updateById(any(ApprovalInstance.class))).thenReturn(1);

        ApprovalInstance result = approvalService.approve(1L, "APPROVE", 2L, "同意");

        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
        assertEquals(2L, result.getCurrentApproverId());
        verify(instanceMapper, times(1)).selectById(1L);
        verify(instanceMapper, times(1)).updateById(any(ApprovalInstance.class));
    }

    @Test
    void approve_withRejectAction_shouldUpdateStatus() {
        when(instanceMapper.selectById(1L)).thenReturn(testInstance);
        when(instanceMapper.updateById(any(ApprovalInstance.class))).thenReturn(1);

        ApprovalInstance result = approvalService.approve(1L, "REJECT", 2L, "不同意");

        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());
        assertEquals(2L, result.getCurrentApproverId());
        verify(instanceMapper, times(1)).selectById(1L);
        verify(instanceMapper, times(1)).updateById(any(ApprovalInstance.class));
    }

    @Test
    void approve_withInvalidId_shouldReturnNull() {
        when(instanceMapper.selectById(999L)).thenReturn(null);

        ApprovalInstance result = approvalService.approve(999L, "APPROVE", 2L, "同意");

        assertNull(result);
        verify(instanceMapper, times(1)).selectById(999L);
        verify(instanceMapper, never()).updateById(any());
    }

    @Test
    void approve_withInvalidAction_shouldNotChangeStatus() {
        when(instanceMapper.selectById(1L)).thenReturn(testInstance);
        when(instanceMapper.updateById(any(ApprovalInstance.class))).thenReturn(1);

        ApprovalInstance result = approvalService.approve(1L, "INVALID", 2L, "test");

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        verify(instanceMapper, times(1)).selectById(1L);
        verify(instanceMapper, times(1)).updateById(any(ApprovalInstance.class));
    }

    @Test
    void approve_withNullApproverId_shouldUpdateStatus() {
        when(instanceMapper.selectById(1L)).thenReturn(testInstance);
        when(instanceMapper.updateById(any(ApprovalInstance.class))).thenReturn(1);

        ApprovalInstance result = approvalService.approve(1L, "APPROVE", null, "同意");

        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
        assertNull(result.getCurrentApproverId());
        verify(instanceMapper, times(1)).selectById(1L);
        verify(instanceMapper, times(1)).updateById(any(ApprovalInstance.class));
    }

    @Test
    void submit_shouldCreateInstanceWithPendingStatus() {
        ApprovalInstance newInstance = new ApprovalInstance();
        newInstance.setTypeId(1L);
        newInstance.setUserId(1L);
        newInstance.setTitle("新建申请");
        newInstance.setContent("新建内容");

        when(instanceMapper.insert(any(ApprovalInstance.class))).thenReturn(1);

        ApprovalInstance result = approvalService.submit(newInstance);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        verify(instanceMapper, times(1)).insert(any(ApprovalInstance.class));
    }

    @Test
    void listByApprover_withValidApproverId_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(instanceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<ApprovalInstance> result = approvalService.listByApprover(2L, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(instanceMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void listByApprover_withNoInstances_shouldReturnEmptyPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList());

        when(instanceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<ApprovalInstance> result = approvalService.listByApprover(999L, 1, 10);

        assertNotNull(result);
        assertTrue(result.getRecords().isEmpty());
        verify(instanceMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void listByApprover_withMultipleInstances_shouldReturnAll() {
        ApprovalInstance testInstance3 = new ApprovalInstance();
        testInstance3.setId(3L);
        testInstance3.setUserId(3L);
        testInstance3.setCurrentApproverId(2L);
        testInstance3.setStatus("PENDING");

        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance, testInstance3));

        when(instanceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<ApprovalInstance> result = approvalService.listByApprover(2L, 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        verify(instanceMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }
}
