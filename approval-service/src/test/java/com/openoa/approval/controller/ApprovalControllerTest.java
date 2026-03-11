package com.openoa.approval.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.approval.BaseTest;
import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.entity.ApprovalType;
import com.openoa.approval.mapper.ApprovalInstanceMapper;
import com.openoa.approval.mapper.ApprovalTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ApprovalControllerTest extends BaseTest {

    @Mock
    private ApprovalInstanceMapper instanceMapper;

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

        when(instanceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.list(1, 10, 1L, "PENDING");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        assertNotNull(result.get("data"));
        verify(instanceMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void list_withOnlyUserId_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(instanceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.list(1, 10, 1L, null);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(instanceMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void list_withOnlyStatus_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(instanceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.list(1, 10, null, "PENDING");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(instanceMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void list_withNoFilters_shouldReturnPage() {
        Page<ApprovalInstance> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testInstance));

        when(instanceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Map<String, Object> result = approvalController.list(1, 10, null, null);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(instanceMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void create_shouldInsertInstanceWithPendingStatus() {
        ApprovalInstance newInstance = new ApprovalInstance();
        newInstance.setTypeId(1L);
        newInstance.setUserId(1L);
        newInstance.setTitle("新建申请");
        newInstance.setContent("新建内容");

        when(instanceMapper.insert(any(ApprovalInstance.class))).thenReturn(1);

        Map<String, Object> result = approvalController.create(newInstance);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("提交成功", result.get("message"));
        assertEquals("PENDING", newInstance.getStatus());
        verify(instanceMapper, times(1)).insert(any(ApprovalInstance.class));
    }

    @Test
    void approve_withApproveAction_shouldUpdateStatusToApproved() {
        when(instanceMapper.selectById(1L)).thenReturn(testInstance);

        Map<String, Object> result = approvalController.approve(1L, "APPROVE", 2L, "同意");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("操作成功", result.get("message"));
        assertEquals("APPROVED", testInstance.getStatus());
        assertEquals(2L, testInstance.getCurrentApproverId());
        verify(instanceMapper, times(1)).selectById(1L);
        verify(instanceMapper, times(1)).updateById(testInstance);
    }

    @Test
    void approve_withRejectAction_shouldUpdateStatusToRejected() {
        when(instanceMapper.selectById(1L)).thenReturn(testInstance);

        Map<String, Object> result = approvalController.approve(1L, "REJECT", 2L, "不同意");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("REJECTED", testInstance.getStatus());
        assertEquals(2L, testInstance.getCurrentApproverId());
        verify(instanceMapper, times(1)).selectById(1L);
        verify(instanceMapper, times(1)).updateById(testInstance);
    }

    @Test
    void approve_withInvalidId_shouldReturn404() {
        when(instanceMapper.selectById(999L)).thenReturn(null);

        Map<String, Object> result = approvalController.approve(999L, "APPROVE", 2L, "同意");

        assertNotNull(result);
        assertEquals(404, result.get("code"));
        assertEquals("审批不存在", result.get("message"));
        verify(instanceMapper, times(1)).selectById(999L);
        verify(instanceMapper, never()).updateById(any());
    }

    @Test
    void getById_withValidId_shouldReturnInstance() {
        when(instanceMapper.selectById(1L)).thenReturn(testInstance);

        Map<String, Object> result = approvalController.getById(1L);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        assertNotNull(result.get("data"));
        verify(instanceMapper, times(1)).selectById(1L);
    }

    @Test
    void getById_withInvalidId_shouldReturnNull() {
        when(instanceMapper.selectById(999L)).thenReturn(null);

        Map<String, Object> result = approvalController.getById(999L);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertNull(result.get("data"));
        verify(instanceMapper, times(1)).selectById(999L);
    }
}
