package com.openoa.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.user.entity.User;
import com.openoa.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer status) {
        Page<User> page = userService.pageList(pageNum, pageSize, username, realName, departmentId, status);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", Map.of(
                "records", page.getRecords(),
                "total", page.getTotal(),
                "size", page.getSize(),
                "current", page.getCurrent(),
                "pages", page.getPages()
        ));
        return result;
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
    public Map<String, Object> create(@Valid @RequestBody User user) {
        if (userService.checkUsernameExists(user.getUsername())) {
            return Map.of(
                    "code", 400,
                    "message", "用户名已存在"
            );
        }
        if (StringUtils.hasText(user.getPhone()) && userService.checkPhoneExists(user.getPhone())) {
            return Map.of(
                    "code", 400,
                    "message", "手机号已存在"
            );
        }
        if (StringUtils.hasText(user.getEmail()) && userService.checkEmailExists(user.getEmail())) {
            return Map.of(
                    "code", 400,
                    "message", "邮箱已存在"
            );
        }
        boolean success = userService.createUser(user);
        if (success) {
            return Map.of(
                    "code", 200,
                    "message", "创建成功",
                    "data", user
            );
        }
        return Map.of(
                "code", 500,
                "message", "创建失败"
        );
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @Valid @RequestBody User user) {
        User existUser = userService.getById(id);
        if (existUser == null) {
            return Map.of(
                    "code", 404,
                    "message", "用户不存在"
            );
        }
        if (userService.checkUsernameExistsExcludeId(user.getUsername(), id)) {
            return Map.of(
                    "code", 400,
                    "message", "用户名已存在"
            );
        }
        if (StringUtils.hasText(user.getPhone()) && userService.checkPhoneExistsExcludeId(user.getPhone(), id)) {
            return Map.of(
                    "code", 400,
                    "message", "手机号已存在"
            );
        }
        if (StringUtils.hasText(user.getEmail()) && userService.checkEmailExistsExcludeId(user.getEmail(), id)) {
            return Map.of(
                    "code", 400,
                    "message", "邮箱已存在"
            );
        }
        boolean success = userService.updateUser(user);
        if (success) {
            return Map.of(
                    "code", 200,
                    "message", "更新成功",
                    "data", user
            );
        }
        return Map.of(
                "code", 500,
                "message", "更新失败"
        );
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        User existUser = userService.getById(id);
        if (existUser == null) {
            return Map.of(
                    "code", 404,
                    "message", "用户不存在"
            );
        }
        boolean success = userService.removeById(id);
        if (success) {
            return Map.of(
                    "code", 200,
                    "message", "删除成功"
            );
        }
        return Map.of(
                "code", 500,
                "message", "删除失败"
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
    
    @GetMapping("/department/{departmentId}/active")
    public Map<String, Object> listActiveByDepartment(@PathVariable Long departmentId) {
        List<User> users = userService.listActiveByDepartment(departmentId);
        return Map.of(
                "code", 200,
                "message", "success",
                "data", users
        );
    }
    
    @PutMapping("/{id}/status")
    public Map<String, Object> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        User existUser = userService.getById(id);
        if (existUser == null) {
            return Map.of(
                    "code", 404,
                    "message", "用户不存在"
            );
        }
        boolean success = userService.updateStatus(id, status);
        if (success) {
            return Map.of(
                    "code", 200,
                    "message", "状态更新成功"
            );
        }
        return Map.of(
                "code", 500,
                "message", "状态更新失败"
        );
    }
    
    @PostMapping("/{id}/password")
    public Map<String, Object> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return Map.of(
                    "code", 400,
                    "message", "参数不完整"
            );
        }
        boolean success = userService.updatePassword(id, oldPassword, newPassword);
        if (success) {
            return Map.of(
                    "code", 200,
                    "message", "密码修改成功"
            );
        }
        return Map.of(
                "code", 400,
                "message", "旧密码不正确或用户不存在"
        );
    }
    
    @PostMapping("/{id}/reset-password")
    public Map<String, Object> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        if (!StringUtils.hasText(newPassword)) {
            return Map.of(
                    "code", 400,
                    "message", "新密码不能为空"
            );
        }
        boolean success = userService.resetPassword(id, newPassword);
        if (success) {
            return Map.of(
                    "code", 200,
                    "message", "密码重置成功"
            );
        }
        return Map.of(
                "code", 404,
                "message", "用户不存在"
        );
    }
    
    @DeleteMapping("/batch")
    public Map<String, Object> batchDelete(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of(
                    "code", 400,
                    "message", "请选择要删除的用户"
            );
        }
        boolean success = userService.batchDelete(ids);
        if (success) {
            return Map.of(
                    "code", 200,
                    "message", "批量删除成功"
            );
        }
        return Map.of(
                "code", 500,
                "message", "批量删除失败"
        );
    }
    
    @PutMapping("/batch/status")
    public Map<String, Object> batchUpdateStatus(@RequestBody Map<String, Object> request) {
        List<Long> ids = (List<Long>) request.get("ids");
        Integer status = (Integer) request.get("status");
        if (ids == null || ids.isEmpty()) {
            return Map.of(
                    "code", 400,
                    "message", "请选择要更新的用户"
            );
        }
        if (status == null) {
            return Map.of(
                    "code", 400,
                    "message", "状态不能为空"
            );
        }
        boolean success = userService.batchUpdateStatus(ids, status);
        if (success) {
            return Map.of(
                    "code", 200,
                    "message", "批量更新成功"
            );
        }
        return Map.of(
                "code", 500,
                "message", "批量更新失败"
        );
    }
    
    @GetMapping("/check/username")
    public Map<String, Object> checkUsername(@RequestParam String username, @RequestParam(required = false) Long excludeId) {
        boolean exists;
        if (excludeId != null) {
            exists = userService.checkUsernameExistsExcludeId(username, excludeId);
        } else {
            exists = userService.checkUsernameExists(username);
        }
        return Map.of(
                "code", 200,
                "message", "success",
                "data", !exists
        );
    }
    
    @GetMapping("/check/phone")
    public Map<String, Object> checkPhone(@RequestParam String phone, @RequestParam(required = false) Long excludeId) {
        boolean exists;
        if (excludeId != null) {
            exists = userService.checkPhoneExistsExcludeId(phone, excludeId);
        } else {
            exists = userService.checkPhoneExists(phone);
        }
        return Map.of(
                "code", 200,
                "message", "success",
                "data", !exists
        );
    }
    
    @GetMapping("/check/email")
    public Map<String, Object> checkEmail(@RequestParam String email, @RequestParam(required = false) Long excludeId) {
        boolean exists;
        if (excludeId != null) {
            exists = userService.checkEmailExistsExcludeId(email, excludeId);
        } else {
            exists = userService.checkEmailExists(email);
        }
        return Map.of(
                "code", 200,
                "message", "success",
                "data", !exists
        );
    }
}
