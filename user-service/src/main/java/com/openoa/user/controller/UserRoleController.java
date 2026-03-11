package com.openoa.user.controller;

import com.openoa.user.common.Result;
import com.openoa.user.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user-roles")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @PostMapping("/assign")
    public Result<Void> assignRoles(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        @SuppressWarnings("unchecked")
        List<Long> roleIds = (List<Long>) request.get("roleIds");
        userRoleService.assignRoles(userId, roleIds);
        return Result.success("角色分配成功", null);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Long>> getRoleIdsByUserId(@PathVariable Long userId) {
        List<Long> roleIds = userRoleService.getRoleIdsByUserId(userId);
        return Result.success(roleIds);
    }
}
