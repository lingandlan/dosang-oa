package com.openoa.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openoa.user.entity.Department;
import com.openoa.user.mapper.DepartmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DepartmentService extends ServiceImpl<DepartmentMapper, Department> {

    public List<Department> listAll(String deptName) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(deptName)) {
            wrapper.like(Department::getDeptName, deptName);
        }
        wrapper.orderByAsc(Department::getSortOrder);
        return list(wrapper);
    }

    public List<Department> listByParentId(Long parentId) {
        return list(new LambdaQueryWrapper<Department>()
                .eq(Department::getParentId, parentId));
    }
}
