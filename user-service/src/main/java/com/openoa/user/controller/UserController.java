package com.openoa.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.user.entity.User;
import com.openoa.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName) {
        Page<User> page = userService.pageList(pageNum, pageSize, username, realName);
        return Map.of(
                "code", 200,
                "message", "success",
                "data", page
        );
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Map.of(
                "code", 200,
                "message", "success",
                "data", user
        );
    }
    
    @PostMapping
    public Map<String, Object> create(@RequestBody User user) {
        userService.save(user);
        return Map.of(
                "code", 200,
                "message", "创建成功",
                "data", user
        );
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateById(user);
        return Map.of(
                "code", 200,
                "message", "更新成功",
                "data", user
        );
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Map.of(
                "code", 200,
                "message", "删除成功"
        );
    }
    
    @GetMapping("/department/{departmentId}")
    public Map<String, Object> listByDepartment(@PathVariable Long departmentId) {
        List<User> users = userService.listByDepartment(departmentId);
        return Map.of(
                "code", 200,
                "message", "success",
                "data", users
        );
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        User user = userService.login(username, password);
        if (user == null) {
            return Map.of(
                    "code", 401,
                    "message", "用户名或密码错误"
            );
        }
        return Map.of(
                "code", 200,
                "message", "登录成功",
                "data", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "realName", user.getRealName(),
                        "email", user.getEmail(),
                        "phone", user.getPhone()
                )
        );
    }
}
