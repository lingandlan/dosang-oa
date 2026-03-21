package com.openoa.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.user.common.Result;
import com.openoa.user.entity.User;
import com.openoa.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
