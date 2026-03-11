package com.openoa.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openoa.user.dto.LoginResponse;
import com.openoa.user.dto.RegisterRequest;
import com.openoa.user.entity.User;
import com.openoa.user.exception.BusinessException;
import com.openoa.user.mapper.UserMapper;
import com.openoa.user.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private JwtUtil jwtUtil;

    public void register(RegisterRequest request) {
        User existUser = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setStatus(1);
        save(user);
    }

    public LoginResponse login(String username, String password) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getEmail(),
                user.getPhone(),
                user.getDepartmentId()
        );

        return new LoginResponse(token, userInfo);
    }

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
