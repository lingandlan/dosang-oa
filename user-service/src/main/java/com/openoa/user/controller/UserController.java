package com.openoa.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.user.common.Result;
import com.openoa.user.dto.LoginRequest;
import com.openoa.user.dto.LoginResponse;
import com.openoa.user.dto.RegisterRequest;
import com.openoa.user.entity.User;
import com.openoa.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success("注册成功", null);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request.getUsername(), request.getPassword());
        return Result.success("登录成功", response);
    }
}

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<Page<User>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName) {
        Page<User> page = userService.pageList(pageNum, pageSize, username, realName);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @PostMapping
    public Result<User> create(@RequestBody User user) {
        userService.save(user);
        return Result.success("创建成功", user);
    }

    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateById(user);
        return Result.success("更新成功", user);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/department/{departmentId}")
    public Result<List<User>> listByDepartment(@PathVariable Long departmentId) {
        List<User> users = userService.listByDepartment(departmentId);
        return Result.success(users);
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
