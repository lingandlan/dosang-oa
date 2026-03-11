package com.openoa.approval.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.approval.BaseTest;
import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.entity.ApprovalType;
import com.openoa.approval.mapper.ApprovalTypeMapper;
import com.openoa.approval.service.ApprovalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApprovalControllerTest extends BaseTest {

    @Mock
    private ApprovalService approvalService;

    @Mock
    private ApprovalTypeMapper typeMapper;

    @InjectMocks
    private ApprovalController approvalController;

    private ApprovalInstance testInstance;
    private ApprovalType testType;

    @BeforeEach
    void setUp() {
        testType = new ApprovalType();
        testType.setId(1L);
        testType.setCode("LEAVE");
        testType.setName("请假");
        testType.setIcon("leave");
        testType.setConfig("{}");
        testType.setCreateTime(LocalDateTime.now());

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
    }

    @Test
    void listTypes_shouldReturnAllTypes() {
        List<ApprovalType> expectedTypes = Arrays.asList(testType);

        when(typeMapper.selectList(null)).thenReturn(expectedTypes);

        Map<String, Object> result = approvalController.listTypes();

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        assertNotNull(result.get("data"));
        @SuppressWarnings("unchecked")
        List<ApprovalType> types = (List<ApprovalType>) result.get("data");
        assertEquals(1, types.size());
        verify(typeMapper, times(1)).selectList(null);
    }

    @Test
    void list_withAllParameters_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(approvalService.pageList(1, 10, 1L, "PENDING"))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.list(1, 10, 1L, "PENDING");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        assertNotNull(result.get("data"));
        verify(approvalService, times(1)).pageList(1, 10, 1L, "PENDING");
    }

    @Test
    void list_withOnlyUserId_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(approvalService.pageList(1, 10, 1L, null))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.list(1, 10, 1L, null);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(approvalService, times(1)).pageList(1, 10, 1L, null);
    }

    @Test
    void list_withOnlyStatus_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(approvalService.pageList(1, 10, null, "PENDING"))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.list(1, 10, null, "PENDING");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(approvalService, times(1)).pageList(1, 10, null, "PENDING");
    }

    @Test
    void list_withNoFilters_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(approvalService.pageList(1, 10, null, null))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.list(1, 10, null, null);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(approvalService, times(1)).pageList(1, 10, null, null);
    }

    @Test
    void create_shouldInsertInstanceWithPendingStatus() {
        ApprovalInstance newInstance = new ApprovalInstance();
        newInstance.setTypeId(1L);
        newInstance.setUserId(1L);
        newInstance.setTitle("新建申请");
        newInstance.setContent("新建内容");

        when(approvalService.submit(any(ApprovalInstance.class))).thenReturn(newInstance);

        Map<String, Object> result = approvalController.create(newInstance);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("提交成功", result.get("message"));
        assertEquals("PENDING", newInstance.getStatus());
        verify(approvalService, times(1)).submit(any(ApprovalInstance.class));
    }

    @Test
    void approve_withApproveAction_shouldUpdateStatusToApproved() {
        when(approvalService.approve(1L, "APPROVE", 2L, "同意")).thenReturn(testInstance);

        Map<String, Object> result = approvalController.approve(1L, "APPROVE", 2L, "同意");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("操作成功", result.get("message"));
        assertEquals("APPROVED", testInstance.getStatus());
        assertEquals(2L, testInstance.getCurrentApproverId());
        verify(approvalService, times(1)).approve(1L, "APPROVE", 2L, "同意");
    }

    @Test
    void approve_withRejectAction_shouldUpdateStatusToRejected() {
        when(approvalService.approve(1L, "REJECT", 2L, "不同意")).thenReturn(testInstance);

        Map<String, Object> result = approvalController.approve(1L, "REJECT", 2L, "不同意");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("REJECTED", testInstance.getStatus());
        assertEquals(2L, testInstance.getCurrentApproverId());
        verify(approvalService, times(1)).approve(1L, "REJECT", 2L, "不同意");
    }

    @Test
    void approve_withInvalidId_shouldReturn404() {
        when(approvalService.approve(999L, "APPROVE", 2L, "同意")).thenReturn(null);

        Map<String, Object> result = approvalController.approve(999L, "APPROVE", 2L, "同意");

        assertNotNull(result);
        assertEquals(404, result.get("code"));
        assertEquals("审批不存在", result.get("message"));
        verify(approvalService, times(1)).approve(999L, "APPROVE", 2L, "同意");
    }

    @Test
    void getById_withValidId_shouldReturnInstance() {
        when(approvalService.getById(1L)).thenReturn(testInstance);

        Map<String, Object> result = approvalController.getById(1L);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        assertNotNull(result.get("data"));
        verify(approvalService, times(1)).getById(1L);
    }

    @Test
    void getById_withInvalidId_shouldReturnNull() {
        when(approvalService.getById(999L)).thenReturn(null);

        Map<String, Object> result = approvalController.getById(999L);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertNull(result.get("data"));
        verify(approvalService, times(1)).getById(999L);
    }

    @Test
    void create_withAllFields_shouldInsertInstance() {
        ApprovalInstance newInstance = new ApprovalInstance();
        newInstance.setTypeId(1L);
        newInstance.setUserId(1L);
        newInstance.setTitle("新建申请");
        newInstance.setContent("新建内容");
        newInstance.setCurrentApproverId(2L);

        when(approvalService.submit(any(ApprovalInstance.class))).thenReturn(newInstance);

        Map<String, Object> result = approvalController.create(newInstance);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("提交成功", result.get("message"));
        assertEquals("PENDING", newInstance.getStatus());
        verify(approvalService, times(1)).submit(any(ApprovalInstance.class));
    }

    @Test
    void approve_withInvalidAction_shouldNotChangeStatus() {
        when(approvalService.approve(1L, "INVALID", 2L, "test")).thenReturn(testInstance);

        Map<String, Object> result = approvalController.approve(1L, "INVALID", 2L, "test");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("PENDING", testInstance.getStatus());
        verify(approvalService, times(1)).approve(1L, "INVALID", 2L, "test");
    }

    @Test
    void approve_withComment_shouldUpdateInstance() {
        when(approvalService.approve(1L, "APPROVE", 2L, "同意申请")).thenReturn(testInstance);

        Map<String, Object> result = approvalController.approve(1L, "APPROVE", 2L, "同意申请");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("APPROVED", testInstance.getStatus());
        verify(approvalService, times(1)).approve(1L, "APPROVE", 2L, "同意申请");
    }

    @Test
    void approve_withNullApproverId_shouldUpdateInstance() {
        when(approvalService.approve(1L, "APPROVE", null, "同意")).thenReturn(testInstance);

        Map<String, Object> result = approvalController.approve(1L, "APPROVE", null, "同意");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("APPROVED", testInstance.getStatus());
        assertNull(testInstance.getCurrentApproverId());
        verify(approvalService, times(1)).approve(1L, "APPROVE", null, "同意");
    }

    @Test
    void approve_withNullComment_shouldUpdateInstance() {
        when(approvalService.approve(1L, "REJECT", 2L, null)).thenReturn(testInstance);

        Map<String, Object> result = approvalController.approve(1L, "REJECT", 2L, null);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("REJECTED", testInstance.getStatus());
        verify(approvalService, times(1)).approve(1L, "REJECT", 2L, null);
    }

    @Test
    void list_withMultipleStatus_shouldReturnFilteredPage() {
        testInstance.setStatus("APPROVED");
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(approvalService.pageList(1, 10, 1L, "APPROVED"))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.list(1, 10, 1L, "APPROVED");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(approvalService, times(1)).pageList(1, 10, 1L, "APPROVED");
    }

    @Test
    void list_withAllFilters_shouldReturnFilteredPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(approvalService.pageList(1, 10, 1L, "PENDING"))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.list(1, 10, 1L, "PENDING");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(approvalService, times(1)).pageList(1, 10, 1L, "PENDING");
    }

    @Test
    void list_withEmptyPage_shouldReturnEmptyPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList());

        when(approvalService.pageList(1, 10, 999L, null))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.list(1, 10, 999L, null);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(approvalService, times(1)).pageList(1, 10, 999L, null);
    }

    @Test
    void listTypes_withMultipleTypes_shouldReturnAll() {
        ApprovalType testType2 = new ApprovalType();
        testType2.setId(2L);
        testType2.setCode("EXPENSE");
        testType2.setName("报销");
        testType2.setIcon("expense");
        testType2.setConfig("{}");
        testType2.setCreateTime(LocalDateTime.now());

        List<ApprovalType> expectedTypes = Arrays.asList(testType, testType2);

        when(typeMapper.selectList(null)).thenReturn(expectedTypes);

        Map<String, Object> result = approvalController.listTypes();

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        @SuppressWarnings("unchecked")
        List<ApprovalType> types = (List<ApprovalType>) result.get("data");
        assertEquals(2, types.size());
        verify(typeMapper, times(1)).selectList(null);
    }

    @Test
    void listTypes_withNoTypes_shouldReturnEmptyList() {
        when(typeMapper.selectList(null)).thenReturn(Arrays.asList());

        Map<String, Object> result = approvalController.listTypes();

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        @SuppressWarnings("unchecked")
        List<ApprovalType> types = (List<ApprovalType>) result.get("data");
        assertTrue(types.isEmpty());
        verify(typeMapper, times(1)).selectList(null);
    }

    @Test
    void listByApprover_withValidApproverId_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(approvalService.listByApprover(2L, 1, 10))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.listByApprover(2L, 1, 10);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(approvalService, times(1)).listByApprover(2L, 1, 10);
    }

    @Test
    void listByApprover_withEmptyPage_shouldReturnEmptyPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList());

        when(approvalService.listByApprover(999L, 1, 10))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.listByApprover(999L, 1, 10);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(approvalService, times(1)).listByApprover(999L, 1, 10);
    }
}
