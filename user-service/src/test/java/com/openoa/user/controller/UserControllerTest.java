package com.openoa.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.user.BaseTest;
import com.openoa.user.entity.User;
import com.openoa.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends BaseTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("zhangsan");
        testUser.setPassword("password123");
        testUser.setRealName("张三");
        testUser.setEmail("zhangsan@example.com");
        testUser.setPhone("13800138001");
        testUser.setDepartmentId(1L);
        testUser.setStatus(1);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void login_withValidCredentials_shouldReturnSuccess() {
        when(userService.login("zhangsan", "password123")).thenReturn(testUser);

        Map<String, Object> request = new HashMap<>();
        request.put("username", "zhangsan");
        request.put("password", "password123");

        Map<String, Object> result = userController.login(request);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("登录成功", result.get("message"));
        assertNotNull(result.get("data"));

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertEquals(1L, data.get("id"));
        assertEquals("zhangsan", data.get("username"));
        assertEquals("张三", data.get("realName"));

        verify(userService, times(1)).login("zhangsan", "password123");
    }

    @Test
    void login_withInvalidCredentials_shouldReturnUnauthorized() {
        when(userService.login("zhangsan", "wrongpassword")).thenReturn(null);

        Map<String, Object> request = new HashMap<>();
        request.put("username", "zhangsan");
        request.put("password", "wrongpassword");

        Map<String, Object> result = userController.login(request);

        assertNotNull(result);
        assertEquals(401, result.get("code"));
        assertEquals("用户名或密码错误", result.get("message"));

        verify(userService, times(1)).login("zhangsan", "wrongpassword");
    }

    @Test
    void login_withDisabledUser_shouldReturnUnauthorized() {
        testUser.setStatus(0);
        when(userService.login("zhangsan", "password123")).thenReturn(null);

        Map<String, Object> request = new HashMap<>();
        request.put("username", "zhangsan");
        request.put("password", "password123");

        Map<String, Object> result = userController.login(request);

        assertNotNull(result);
        assertEquals(401, result.get("code"));

        verify(userService, times(1)).login("zhangsan", "password123");
    }

    @Test
    void login_withEmptyUsername_shouldReturnUnauthorized() {
        when(userService.login("", "password123")).thenReturn(null);

        Map<String, Object> request = new HashMap<>();
        request.put("username", "");
        request.put("password", "password123");

        Map<String, Object> result = userController.login(request);

        assertNotNull(result);
        assertEquals(401, result.get("code"));

        verify(userService, times(1)).login("", "password123");
    }

    @Test
    void login_withEmptyPassword_shouldReturnUnauthorized() {
        when(userService.login("zhangsan", "")).thenReturn(null);

        Map<String, Object> request = new HashMap<>();
        request.put("username", "zhangsan");
        request.put("password", "");

        Map<String, Object> result = userController.login(request);

        assertNotNull(result);
        assertEquals(401, result.get("code"));

        verify(userService, times(1)).login("zhangsan", "");
    }

    @Test
    void list_withAllParameters_shouldReturnPage() {
        Page<User> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testUser));

        when(userService.pageList(1, 10, "zhang", "张")).thenReturn(expectedPage);

        Map<String, Object> result = userController.list(1, 10, "zhang", "张");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        assertNotNull(result.get("data"));
        verify(userService, times(1)).pageList(1, 10, "zhang", "张");
    }

    @Test
    void list_withOnlyUsername_shouldReturnPage() {
        Page<User> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testUser));

        when(userService.pageList(1, 10, "zhang", null)).thenReturn(expectedPage);

        Map<String, Object> result = userController.list(1, 10, "zhang", null);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(userService, times(1)).pageList(1, 10, "zhang", null);
    }

    @Test
    void list_withOnlyRealName_shouldReturnPage() {
        Page<User> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testUser));

        when(userService.pageList(1, 10, null, "张")).thenReturn(expectedPage);

        Map<String, Object> result = userController.list(1, 10, null, "张");

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(userService, times(1)).pageList(1, 10, null, "张");
    }

    @Test
    void list_withNoFilters_shouldReturnPage() {
        Page<User> expectedPage = new Page<>(1, 10);
        expectedPage.setRecords(Arrays.asList(testUser));

        when(userService.pageList(1, 10, null, null)).thenReturn(expectedPage);

        Map<String, Object> result = userController.list(1, 10, null, null);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        verify(userService, times(1)).pageList(1, 10, null, null);
    }

    @Test
    void getById_withValidId_shouldReturnUser() {
        when(userService.getById(1L)).thenReturn(testUser);

        Map<String, Object> result = userController.getById(1L);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertNotNull(result.get("data"));
        verify(userService, times(1)).getById(1L);
    }

    @Test
    void getById_withInvalidId_shouldReturnNull() {
        when(userService.getById(999L)).thenReturn(null);

        Map<String, Object> result = userController.getById(999L);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertNull(result.get("data"));
        verify(userService, times(1)).getById(999L);
    }

    @Test
    void create_withValidUser_shouldReturnSuccess() {
        when(userService.save(any(User.class))).thenReturn(true);

        Map<String, Object> result = userController.create(testUser);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("创建成功", result.get("message"));
        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    void update_withValidUser_shouldReturnSuccess() {
        when(userService.updateById(any(User.class))).thenReturn(true);

        Map<String, Object> result = userController.update(1L, testUser);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("更新成功", result.get("message"));
        assertEquals(1L, testUser.getId());
        verify(userService, times(1)).updateById(any(User.class));
    }

    @Test
    void delete_withValidId_shouldReturnSuccess() {
        when(userService.removeById(1L)).thenReturn(true);

        Map<String, Object> result = userController.delete(1L);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("删除成功", result.get("message"));
        verify(userService, times(1)).removeById(1L);
    }

    @Test
    void listByDepartment_withValidDepartmentId_shouldReturnUsers() {
        when(userService.listByDepartment(1L)).thenReturn(Arrays.asList(testUser));

        Map<String, Object> result = userController.listByDepartment(1L);

        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertNotNull(result.get("data"));
        verify(userService, times(1)).listByDepartment(1L);
    }
}
