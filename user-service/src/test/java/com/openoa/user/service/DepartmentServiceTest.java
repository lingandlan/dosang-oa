package com.openoa.user.service;

import com.openoa.user.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @BeforeEach
    public void setUp() {
        // 清理测试数据
        departmentService.remove(null);
    }

    @Test
    public void testListAll() {
        // 创建测试部门
        Department dept1 = new Department();
        dept1.setDeptName("技术部");
        dept1.setDeptCode("TECH");
        dept1.setSortOrder(1);
        dept1.setParentId(0L);
        dept1.setStatus(1);
        departmentService.save(dept1);

        Department dept2 = new Department();
        dept2.setDeptName("市场部");
        dept2.setDeptCode("MARKET");
        dept2.setSortOrder(2);
        dept2.setParentId(0L);
        dept2.setStatus(1);
        departmentService.save(dept2);

        // 查询所有部门
        List<Department> departments = departmentService.listAll(null);

        assertNotNull(departments);
        assertEquals(2, departments.size());
    }

    @Test
    public void testListAllWithFilter() {
        // 创建测试部门
        Department dept1 = new Department();
        dept1.setDeptName("技术部研发组");
        dept1.setDeptCode("TECH_DEV");
        dept1.setSortOrder(1);
        dept1.setParentId(0L);
        dept1.setStatus(1);
        departmentService.save(dept1);

        Department dept2 = new Department();
        dept2.setDeptName("技术部测试组");
        dept2.setDeptCode("TECH_TEST");
        dept2.setSortOrder(2);
        dept2.setParentId(0L);
        dept2.setStatus(1);
        departmentService.save(dept2);

        Department dept3 = new Department();
        dept3.setDeptName("市场部");
        dept3.setDeptCode("MARKET");
        dept3.setSortOrder(3);
        dept3.setParentId(0L);
        dept3.setStatus(1);
        departmentService.save(dept3);

        // 按名称模糊查询
        List<Department> departments = departmentService.listAll("技术部");

        assertNotNull(departments);
        assertEquals(2, departments.size());
    }

    @Test
    public void testListByParentId() {
        // 创建父部门
        Department parentDept = new Department();
        parentDept.setDeptName("技术部");
        parentDept.setDeptCode("TECH");
        parentDept.setSortOrder(1);
        parentDept.setParentId(0L);
        parentDept.setStatus(1);
        departmentService.save(parentDept);

        Long parentId = parentDept.getId();

        // 创建子部门
        Department childDept1 = new Department();
        childDept1.setDeptName("研发组");
        childDept1.setDeptCode("TECH_DEV");
        childDept1.setSortOrder(1);
        childDept1.setParentId(parentId);
        childDept1.setStatus(1);
        departmentService.save(childDept1);

        Department childDept2 = new Department();
        childDept2.setDeptName("测试组");
        childDept2.setDeptCode("TECH_TEST");
        childDept2.setSortOrder(2);
        childDept2.setParentId(parentId);
        childDept2.setStatus(1);
        departmentService.save(childDept2);

        // 查询子部门
        List<Department> children = departmentService.listByParentId(parentId);

        assertNotNull(children);
        assertEquals(2, children.size());
    }

    @Test
    public void testSaveAndGetById() {
        Department dept = new Department();
        dept.setDeptName("财务部");
        dept.setDeptCode("FINANCE");
        dept.setSortOrder(5);
        dept.setParentId(0L);
        dept.setStatus(1);
        dept.setRemark("财务管理部");

        departmentService.save(dept);

        Department savedDept = departmentService.getById(dept.getId());

        assertNotNull(savedDept);
        assertEquals("财务部", savedDept.getDeptName());
        assertEquals("FINANCE", savedDept.getDeptCode());
    }

    @Test
    public void testUpdateDepartment() {
        Department dept = new Department();
        dept.setDeptName("人力资源部");
        dept.setDeptCode("HR");
        dept.setSortOrder(6);
        dept.setParentId(0L);
        dept.setStatus(1);
        departmentService.save(dept);

        Long deptId = dept.getId();

        // 更新部门
        Department updateDept = new Department();
        updateDept.setId(deptId);
        updateDept.setDeptName("人力资源部（更新）");
        updateDept.setDeptCode("HR");
        updateDept.setSortOrder(7);
        updateDept.setParentId(0L);
        updateDept.setStatus(1);
        departmentService.updateById(updateDept);

        Department updatedDept = departmentService.getById(deptId);

        assertEquals("人力资源部（更新）", updatedDept.getDeptName());
        assertEquals(7, updatedDept.getSortOrder());
    }

    @Test
    public void testDeleteDepartment() {
        Department dept = new Department();
        dept.setDeptName("测试部");
        dept.setDeptCode("TEST");
        dept.setSortOrder(10);
        dept.setParentId(0L);
        dept.setStatus(1);
        departmentService.save(dept);

        Long deptId = dept.getId();

        departmentService.removeById(deptId);

        Department deletedDept = departmentService.getById(deptId);

        assertNull(deletedDept);
    }
}
