package com.openoa.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openoa.user.entity.RolePermission;
import com.openoa.user.mapper.RolePermissionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePermissionService extends ServiceImpl<RolePermissionMapper, RolePermission> {

    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        remove(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                save(rolePermission);
            }
        }
    }

    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return list(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getRoleId, roleId))
                .stream()
                .map(RolePermission::getPermissionId)
                .toList();
    }
}
