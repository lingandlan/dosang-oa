package com.openoa.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openoa.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    @Select("SELECT * FROM sys_user WHERE phone = #{phone}")
    User findByPhone(@Param("phone") String phone);
    
    @Select("SELECT * FROM sys_user WHERE email = #{email}")
    User findByEmail(@Param("email") String email);
    
    @Select("SELECT * FROM sys_user WHERE department_id = #{departmentId} AND status = 1")
    List<User> listActiveByDepartment(@Param("departmentId") Long departmentId);
    
    @Select("SELECT COUNT(*) FROM sys_user WHERE username = #{username} AND id != #{id}")
    int countByUsernameExcludeId(@Param("username") String username, @Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM sys_user WHERE phone = #{phone} AND id != #{id}")
    int countByPhoneExcludeId(@Param("phone") String phone, @Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM sys_user WHERE email = #{email} AND id != #{id}")
    int countByEmailExcludeId(@Param("email") String email, @Param("id") Long id);
}
