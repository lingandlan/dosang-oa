package com.openoa.approval.mapper;

import com.openoa.approval.BaseTest;
import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.entity.ApprovalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApprovalMapperTest extends BaseTest {

    @Autowired
    private ApprovalInstanceMapper instanceMapper;

    @Autowired
    private ApprovalTypeMapper typeMapper;

    private ApprovalType testType;
    private ApprovalInstance testInstance;

    @BeforeEach
    void setUp() {
        instanceMapper.delete(null);
        typeMapper.delete(null);

        testType = new ApprovalType();
        testType.setCode("LEAVE");
        testType.setName("请假");
        testType.setIcon("leave");
        testType.setConfig("{}");
        testType.setCreateTime(LocalDateTime.now());
        typeMapper.insert(testType);

        testInstance = new ApprovalInstance();
        testInstance.setTypeId(testType.getId());
        testInstance.setUserId(1L);
        testInstance.setTitle("请假申请");
        testInstance.setContent("因事请假一天");
        testInstance.setStatus("PENDING");
        testInstance.setCurrentApproverId(2L);
        testInstance.setCreateTime(LocalDateTime.now());
        testInstance.setUpdateTime(LocalDateTime.now());
        instanceMapper.insert(testInstance);
    }

    @Test
    void insertApprovalType_shouldReturnId() {
        ApprovalType newType = new ApprovalType();
        newType.setCode("EXPENSE");
        newType.setName("报销");
        newType.setIcon("expense");
        newType.setConfig("{}");
        newType.setCreateTime(LocalDateTime.now());

        int result = typeMapper.insert(newType);

        assertTrue(result > 0);
        assertNotNull(newType.getId());
    }

    @Test
    void selectById_shouldReturnApprovalType() {
        ApprovalType result = typeMapper.selectById(testType.getId());

        assertNotNull(result);
        assertEquals("LEAVE", result.getCode());
        assertEquals("请假", result.getName());
    }

    @Test
    void selectList_shouldReturnAllTypes() {
        List<ApprovalType> types = typeMapper.selectList(null);

        assertNotNull(types);
        assertTrue(types.size() >= 1);
        assertTrue(types.stream().anyMatch(t -> "LEAVE".equals(t.getCode())));
    }

    @Test
    void updateById_shouldUpdateApprovalType() {
        testType.setName("请假审批");
        int result = typeMapper.updateById(testType);

        assertTrue(result > 0);

        ApprovalType updated = typeMapper.selectById(testType.getId());
        assertEquals("请假审批", updated.getName());
    }

    @Test
    void deleteById_shouldDeleteApprovalType() {
        int result = typeMapper.deleteById(testType.getId());

        assertTrue(result > 0);

        ApprovalType deleted = typeMapper.selectById(testType.getId());
        assertNull(deleted);
    }

    @Test
    void insertApprovalInstance_shouldReturnId() {
        ApprovalInstance newInstance = new ApprovalInstance();
        newInstance.setTypeId(testType.getId());
        newInstance.setUserId(2L);
        newInstance.setTitle("报销申请");
        newInstance.setContent("差旅报销");
        newInstance.setStatus("PENDING");
        newInstance.setCurrentApproverId(3L);
        newInstance.setCreateTime(LocalDateTime.now());
        newInstance.setUpdateTime(LocalDateTime.now());

        int result = instanceMapper.insert(newInstance);

        assertTrue(result > 0);
        assertNotNull(newInstance.getId());
    }

    @Test
    void selectById_shouldReturnApprovalInstance() {
        ApprovalInstance result = instanceMapper.selectById(testInstance.getId());

        assertNotNull(result);
        assertEquals("请假申请", result.getTitle());
        assertEquals("PENDING", result.getStatus());
    }

    @Test
    void selectList_shouldReturnAllInstances() {
        List<ApprovalInstance> instances = instanceMapper.selectList(null);

        assertNotNull(instances);
        assertTrue(instances.size() >= 1);
        assertTrue(instances.stream().anyMatch(i -> "请假申请".equals(i.getTitle())));
    }

    @Test
    void updateById_shouldUpdateApprovalInstance() {
        testInstance.setStatus("APPROVED");
        testInstance.setCurrentApproverId(3L);
        int result = instanceMapper.updateById(testInstance);

        assertTrue(result > 0);

        ApprovalInstance updated = instanceMapper.selectById(testInstance.getId());
        assertEquals("APPROVED", updated.getStatus());
        assertEquals(3L, updated.getCurrentApproverId());
    }

    @Test
    void deleteById_shouldDeleteApprovalInstance() {
        int result = instanceMapper.deleteById(testInstance.getId());

        assertTrue(result > 0);

        ApprovalInstance deleted = instanceMapper.selectById(testInstance.getId());
        assertNull(deleted);
    }
}
