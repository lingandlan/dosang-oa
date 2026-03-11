package com.openoa.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.user.BaseTest;
import com.openoa.user.entity.User;
import com.openoa.user.mapper.UserMapper;
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

class UserServiceTest extends BaseTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setUsername("zhangsan");
        testUser1.setPassword("password123");
        testUser1.setRealName("张三");
        testUser1.setEmail("zhangsan@example.com");
        testUser1.setPhone("13800138001");
        testUser1.setDepartmentId(1L);
        testUser1.setStatus(1);
        testUser1.setCreateTime(LocalDateTime.now());
        testUser1.setUpdateTime(LocalDateTime.now());

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("lisi");
        testUser2.setPassword("password456");
        testUser2.setRealName("李四");
        testUser2.setEmail("lisi@example.com");
        testUser2.setPhone("13800138002");
        testUser2.setDepartmentId(2L);
        testUser2.setStatus(1);
        testUser2.setCreateTime(LocalDateTime.now());
        testUser2.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void pageList_withAllParameters_shouldReturnPage() {
        Page<User> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testUser1, testUser2));

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<User> result = userService.pageList(1, 10, "zhang", "张");

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageList_withOnlyUsername_shouldReturnPage() {
        Page<User> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testUser1));

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<User> result = userService.pageList(1, 10, "zhang", null);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageList_withOnlyRealName_shouldReturnPage() {
        Page<User> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testUser2));

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<User> result = userService.pageList(1, 10, null, "李");

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageList_withNoFilters_shouldReturnPage() {
        Page<User> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testUser1, testUser2));

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        Page<User> result = userService.pageList(1, 10, null, null);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void listByDepartment_withValidDepartmentId_shouldReturnUsers() {
        List<User> expectedUsers = Arrays.asList(testUser1);

        when(userMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(expectedUsers);

        List<User> result = userService.listByDepartment(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getDepartmentId());
        verify(userMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void listByDepartment_withNoUsers_shouldReturnEmptyList() {
        when(userMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList());

        List<User> result = userService.listByDepartment(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void listByDepartment_withMultipleUsers_shouldReturnAllUsers() {
        User testUser3 = new User();
        testUser3.setId(3L);
        testUser3.setUsername("wangwu");
        testUser3.setPassword("password789");
        testUser3.setRealName("王五");
        testUser3.setDepartmentId(1L);
        testUser3.setStatus(1);

        List<User> expectedUsers = Arrays.asList(testUser1, testUser3);

        when(userMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(expectedUsers);

        List<User> result = userService.listByDepartment(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }
}
