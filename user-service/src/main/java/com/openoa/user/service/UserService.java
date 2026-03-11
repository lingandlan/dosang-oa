package com.openoa.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openoa.user.entity.User;
import com.openoa.user.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public Page<User> pageList(Integer pageNum, Integer pageSize, String username, String realName, Long departmentId, Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(realName)) {
            wrapper.like(User::getRealName, realName);
        }
        if (departmentId != null) {
            wrapper.eq(User::getDepartmentId, departmentId);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return page(page, wrapper);
    }
    
    public List<User> listByDepartment(Long departmentId) {
        return list(new LambdaQueryWrapper<User>()
                .eq(User::getDepartmentId, departmentId));
    }
    
    public User findByUsername(String username) {
        return baseMapper.findByUsername(username);
    }
    
    public User findByPhone(String phone) {
        return baseMapper.findByPhone(phone);
    }
    
    public User findByEmail(String email) {
        return baseMapper.findByEmail(email);
    }
    
    public List<User> listActiveByDepartment(Long departmentId) {
        return baseMapper.listActiveByDepartment(departmentId);
    }
    
    @Transactional
    public boolean createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        return save(user);
    }
    
    @Transactional
    public boolean updateUser(User user) {
        User existUser = getById(user.getId());
        if (existUser == null) {
            return false;
        }
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(existUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return updateById(user);
    }
    
    @Transactional
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return updateById(user);
    }
    
    @Transactional
    public boolean resetPassword(Long userId, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return updateById(user);
    }
    
    @Transactional
    public boolean updateStatus(Long userId, Integer status) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        user.setStatus(status);
        return updateById(user);
    }
    
    public boolean checkUsernameExists(String username) {
        return baseMapper.findByUsername(username) != null;
    }
    
    public boolean checkUsernameExistsExcludeId(String username, Long id) {
        return baseMapper.countByUsernameExcludeId(username, id) > 0;
    }
    
    public boolean checkPhoneExists(String phone) {
        return baseMapper.findByPhone(phone) != null;
    }
    
    public boolean checkPhoneExistsExcludeId(String phone, Long id) {
        return baseMapper.countByPhoneExcludeId(phone, id) > 0;
    }
    
    public boolean checkEmailExists(String email) {
        return baseMapper.findByEmail(email) != null;
    }
    
    public boolean checkEmailExistsExcludeId(String email, Long id) {
        return baseMapper.countByEmailExcludeId(email, id) > 0;
    }
    
    @Transactional
    public boolean batchDelete(List<Long> ids) {
        return removeBatchByIds(ids);
    }
    
    @Transactional
    public boolean batchUpdateStatus(List<Long> ids, Integer status) {
        return ids.stream().allMatch(id -> updateStatus(id, status));
    }
}
