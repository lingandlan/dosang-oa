package com.openoa.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openoa.user.entity.Role;
import com.openoa.user.mapper.RoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {

    public List<Role> listAll(String roleName) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }
        return list(wrapper);
    }

    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        remove(new LambdaQueryWrapper<Role>().eq(Role::getId, roleId));
    }
}
