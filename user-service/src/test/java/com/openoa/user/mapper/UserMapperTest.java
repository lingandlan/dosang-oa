package com.openoa.user.mapper;

import com.openoa.user.BaseTest;
import com.openoa.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest extends BaseTest {

    @Autowired
    private UserMapper userMapper;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        userMapper.delete(null);

        testUser1 = new User();
        testUser1.setUsername("zhangsan");
        testUser1.setPassword("password123");
        testUser1.setRealName("张三");
        testUser1.setEmail("zhangsan@example.com");
        testUser1.setPhone("13800138001");
        testUser1.setDepartmentId(1L);
        testUser1.setStatus(1);
        testUser1.setCreateTime(LocalDateTime.now());
        testUser1.setUpdateTime(LocalDateTime.now());
        userMapper.insert(testUser1);

        testUser2 = new User();
        testUser2.setUsername("lisi");
        testUser2.setPassword("password456");
        testUser2.setRealName("李四");
        testUser2.setEmail("lisi@example.com");
        testUser2.setPhone("13800138002");
        testUser2.setDepartmentId(2L);
        testUser2.setStatus(1);
        testUser2.setCreateTime(LocalDateTime.now());
        testUser2.setUpdateTime(LocalDateTime.now());
        userMapper.insert(testUser2);
    }

    @Test
    void insertUser_shouldReturnId() {
        User newUser = new User();
        newUser.setUsername("wangwu");
        newUser.setPassword("password789");
        newUser.setRealName("王五");
        newUser.setEmail("wangwu@example.com");
        newUser.setPhone("13800138003");
        newUser.setDepartmentId(1L);
        newUser.setStatus(1);
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());

        int result = userMapper.insert(newUser);

        assertTrue(result > 0);
        assertNotNull(newUser.getId());
    }

    @Test
    void selectById_shouldReturnUser() {
        User result = userMapper.selectById(testUser1.getId());

        assertNotNull(result);
        assertEquals("zhangsan", result.getUsername());
        assertEquals("张三", result.getRealName());
    }

    @Test
    void selectById_withNonExistentId_shouldReturnNull() {
        User result = userMapper.selectById(999L);

        assertNull(result);
    }

    @Test
    void selectList_shouldReturnAllUsers() {
        List<User> users = userMapper.selectList(null);

        assertNotNull(users);
        assertTrue(users.size() >= 2);
        assertTrue(users.stream().anyMatch(u -> "zhangsan".equals(u.getUsername())));
        assertTrue(users.stream().anyMatch(u -> "lisi".equals(u.getUsername())));
    }

    @Test
    void updateById_shouldUpdateUser() {
        testUser1.setRealName("张三丰");
        testUser1.setEmail("zhangsanfeng@example.com");
        int result = userMapper.updateById(testUser1);

        assertTrue(result > 0);

        User updated = userMapper.selectById(testUser1.getId());
        assertEquals("张三丰", updated.getRealName());
        assertEquals("zhangsanfeng@example.com", updated.getEmail());
    }

    @Test
    void deleteById_shouldDeleteUser() {
        int result = userMapper.deleteById(testUser1.getId());

        assertTrue(result > 0);

        User deleted = userMapper.selectById(testUser1.getId());
        assertNull(deleted);
    }

    @Test
    void selectOne_withUsernameAndPassword_shouldReturnUser() {
        User result = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, "zhangsan")
                .eq(User::getPassword, "password123")
                .eq(User::getStatus, 1)
        );

        assertNotNull(result);
        assertEquals("zhangsan", result.getUsername());
    }

    @Test
    void selectOne_withWrongPassword_shouldReturnNull() {
        User result = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, "zhangsan")
                .eq(User::getPassword, "wrongpassword")
                .eq(User::getStatus, 1)
        );

        assertNull(result);
    }

    @Test
    void selectList_withDepartmentId_shouldReturnUsers() {
        List<User> users = userMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getDepartmentId, 1L)
        );

        assertNotNull(users);
        assertTrue(users.stream().anyMatch(u -> "zhangsan".equals(u.getUsername())));
        assertTrue(users.stream().allMatch(u -> 1L.equals(u.getDepartmentId())));
    }
}
