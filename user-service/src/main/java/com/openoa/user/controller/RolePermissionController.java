package com.openoa.user.controller;

import com.openoa.user.common.Result;
import com.openoa.user.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/role-permissions")
public class RolePermissionController {

    @Autowired
    private RolePermissionService rolePermissionService;

    @PostMapping("/assign")
    public Result<Void> assignPermissions(@RequestBody Map<String, Object> request) {
        Long roleId = Long.valueOf(request.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> permissionIds = (List<Long>) request.get("permissionIds");
        rolePermissionService.assignPermissions(roleId, permissionIds);
        return Result.success("权限分配成功", null);
    }

    @GetMapping("/role/{roleId}")
    public Result<List<Long>> getPermissionIdsByRoleId(@PathVariable Long roleId) {
        List<Long> permissionIds = rolePermissionService.getPermissionIdsByRoleId(roleId);
        return Result.success(permissionIds);
    }
}
