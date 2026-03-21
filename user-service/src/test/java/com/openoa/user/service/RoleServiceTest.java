package com.openoa.user.service;

import com.openoa.user.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @BeforeEach
    public void setUp() {
        // 清理测试数据
        roleService.remove(null);
    }

    @Test
    public void testListAll() {
        // 创建测试角色
        Role role1 = new Role();
        role1.setRoleName("管理员");
        role1.setRoleCode("ADMIN");
        role1.setDescription("系统管理员");
        role1.setStatus(1);
        roleService.save(role1);

        Role role2 = new Role();
        role2.setRoleName("普通用户");
        role2.setRoleCode("USER");
        role2.setDescription("普通用户角色");
        role2.setStatus(1);
        roleService.save(role2);

        // 查询所有角色
        List<Role> roles = roleService.listAll(null);

        assertNotNull(roles);
        assertEquals(2, roles.size());
    }

    @Test
    public void testListAllWithFilter() {
        // 创建测试角色
        Role role1 = new Role();
        role1.setRoleName("系统管理员");
        role1.setRoleCode("SYS_ADMIN");
        role1.setDescription("系统管理员");
        role1.setStatus(1);
        roleService.save(role1);

        Role role2 = new Role();
        role2.setRoleName("部门管理员");
        role2.setRoleCode("DEPT_ADMIN");
        role2.setDescription("部门管理员");
        role2.setStatus(1);
        roleService.save(role2);

        Role role3 = new Role();
        role3.setRoleName("普通用户");
        role3.setRoleCode("USER");
        role3.setDescription("普通用户");
        role3.setStatus(1);
        roleService.save(role3);

        // 按角色名模糊查询
        List<Role> roles = roleService.listAll("管理员");

        assertNotNull(roles);
        assertEquals(2, roles.size());
    }

    @Test
    public void testSaveAndGetById() {
        Role role = new Role();
        role.setRoleName("测试角色");
        role.setRoleCode("TEST_ROLE");
        role.setDescription("测试用角色");
        role.setStatus(1);

        roleService.save(role);

        Role savedRole = roleService.getById(role.getId());

        assertNotNull(savedRole);
        assertEquals("测试角色", savedRole.getRoleName());
        assertEquals("TEST_ROLE", savedRole.getRoleCode());
    }

    @Test
    public void testUpdateRole() {
        Role role = new Role();
        role.setRoleName("原始角色名");
        role.setRoleCode("ORIGINAL_ROLE");
        role.setDescription("原始描述");
        role.setStatus(1);
        roleService.save(role);

        Long roleId = role.getId();

        // 更新角色
        Role updateRole = new Role();
        updateRole.setId(roleId);
        updateRole.setRoleName("更新后的角色名");
        updateRole.setRoleCode("UPDATED_ROLE");
        updateRole.setDescription("更新后的描述");
        updateRole.setStatus(1);
        roleService.updateById(updateRole);

        Role updatedRole = roleService.getById(roleId);

        assertEquals("更新后的角色名", updatedRole.getRoleName());
        assertEquals("UPDATED_ROLE", updatedRole.getRoleCode());
    }

    @Test
    public void testDeleteRole() {
        Role role = new Role();
        role.setRoleName("待删除角色");
        role.setRoleCode("TO_DELETE");
        role.setDescription("将被删除的角色");
        role.setStatus(1);
        roleService.save(role);

        Long roleId = role.getId();

        roleService.removeById(roleId);

        Role deletedRole = roleService.getById(roleId);

        assertNull(deletedRole);
    }

    @Test
    public void testUniqueRoleCode() {
        // 创建第一个角色
        Role role1 = new Role();
        role1.setRoleName("角色A");
        role1.setRoleCode("UNIQUE_TEST");
        role1.setDescription("第一个角色");
        role1.setStatus(1);
        roleService.save(role1);

        // 尝试创建相同roleCode的角色
        Role role2 = new Role();
        role2.setRoleName("角色B");
        role2.setRoleCode("UNIQUE_TEST");
        role2.setDescription("第二个角色");
        role2.setStatus(1);

        // 由于数据库有唯一约束，这应该失败
        assertThrows(Exception.class, () -> {
            roleService.save(role2);
        });
    }
}
