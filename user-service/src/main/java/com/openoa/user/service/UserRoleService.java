package com.openoa.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openoa.user.entity.UserRole;
import com.openoa.user.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService extends ServiceImpl<UserRoleMapper, UserRole> {

    public void assignRoles(Long userId, List<Long> roleIds) {
        remove(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                save(userRole);
            }
        }
    }

    public List<Long> getRoleIdsByUserId(Long userId) {
        return list(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId))
                .stream()
                .map(UserRole::getRoleId)
                .toList();
    }
}
