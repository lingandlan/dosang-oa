package com.openoa.user.controller;

import com.openoa.user.common.Result;
import com.openoa.user.entity.Permission;
import com.openoa.user.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public Result<List<Permission>> list(@RequestParam(required = false) String permissionName) {
        List<Permission> list = permissionService.listAll(permissionName);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        return Result.success(permission);
    }

    @GetMapping("/tree")
    public Result<List<Permission>> getTree() {
        List<Permission> list = permissionService.listAll(null);
        return Result.success(buildTree(list, 0L));
    }

    @GetMapping("/parent/{parentId}")
    public Result<List<Permission>> listByParentId(@PathVariable Long parentId) {
        List<Permission> list = permissionService.listByParentId(parentId);
        return Result.success(list);
    }

    @PostMapping
    public Result<Permission> create(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.success("创建成功", permission);
    }

    @PutMapping("/{id}")
    public Result<Permission> update(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        permissionService.updateById(permission);
        return Result.success("更新成功", permission);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.removeById(id);
        return Result.success("删除成功", null);
    }

    private List<Permission> buildTree(List<Permission> list, Long parentId) {
        return list.stream()
                .filter(perm -> parentId.equals(perm.getParentId()))
                .peek(perm -> perm.setChildren(buildTree(list, perm.getId())))
                .toList();
    }
}
