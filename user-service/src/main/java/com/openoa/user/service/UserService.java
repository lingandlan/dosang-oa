package com.openoa.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openoa.user.entity.User;
import com.openoa.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    public Page<User> pageList(Integer pageNum, Integer pageSize, String username, String realName) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(realName)) {
            wrapper.like(User::getRealName, realName);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return page(page, wrapper);
    }
    
    public List<User> listByDepartment(Long departmentId) {
        return list(new LambdaQueryWrapper<User>()
                .eq(User::getDepartmentId, departmentId));
    }

    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .eq(User::getPassword, password)
                .eq(User::getStatus, 1);
        return getOne(wrapper);
    }
}
