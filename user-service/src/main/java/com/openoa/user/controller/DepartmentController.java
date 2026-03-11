package com.openoa.user.controller;

import com.openoa.user.common.Result;
import com.openoa.user.entity.Department;
import com.openoa.user.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public Result<List<Department>> list(@RequestParam(required = false) String deptName) {
        List<Department> list = departmentService.listAll(deptName);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Department> getById(@PathVariable Long id) {
        Department department = departmentService.getById(id);
        return Result.success(department);
    }

    @GetMapping("/tree")
    public Result<List<Department>> getTree() {
        List<Department> list = departmentService.listAll(null);
        return Result.success(buildTree(list, 0L));
    }

    @GetMapping("/parent/{parentId}")
    public Result<List<Department>> listByParentId(@PathVariable Long parentId) {
        List<Department> list = departmentService.listByParentId(parentId);
        return Result.success(list);
    }

    @PostMapping
    public Result<Department> create(@RequestBody Department department) {
        departmentService.save(department);
        return Result.success("创建成功", department);
    }

    @PutMapping("/{id}")
    public Result<Department> update(@PathVariable Long id, @RequestBody Department department) {
        department.setId(id);
        departmentService.updateById(department);
        return Result.success("更新成功", department);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.removeById(id);
        return Result.success("删除成功", null);
    }

    private List<Department> buildTree(List<Department> list, Long parentId) {
        return list.stream()
                .filter(dept -> parentId.equals(dept.getParentId()))
                .peek(dept -> dept.setChildren(buildTree(list, dept.getId())))
                .toList();
    }
}
