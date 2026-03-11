package com.openoa.user.controller;

import com.openoa.user.common.Result;
import com.openoa.user.entity.Role;
import com.openoa.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public Result<List<Role>> list(@RequestParam(required = false) String roleName) {
        List<Role> list = roleService.listAll(roleName);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }

    @PostMapping
    public Result<Role> create(@RequestBody Role role) {
        roleService.save(role);
        return Result.success("创建成功", role);
    }

    @PutMapping("/{id}")
    public Result<Role> update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        roleService.updateById(role);
        return Result.success("更新成功", role);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.success("删除成功", null);
    }
}
