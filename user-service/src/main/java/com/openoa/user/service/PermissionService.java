package com.openoa.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openoa.user.entity.Permission;
import com.openoa.user.mapper.PermissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> {

    public List<Permission> listAll(String permissionName) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(permissionName)) {
            wrapper.like(Permission::getPermissionName, permissionName);
        }
        wrapper.orderByAsc(Permission::getSortOrder);
        return list(wrapper);
    }

    public List<Permission> listByParentId(Long parentId) {
        return list(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getParentId, parentId));
    }
}
