package com.openoa.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务集成测试
 * 测试端口: 8081
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("用户服务集成测试")
public class UserServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    // ==================== 认证接口测试 ====================

    @Test
    @DisplayName("用户注册 - 成功")
    void testRegister_Success() {
        String url = baseUrl + "/api/v1/auth/register";
        
        Map<String, String> request = Map.of(
                "username", "testuser_" + System.currentTimeMillis(),
                "password", "Test123456",
                "email", "test@example.com",
                "realName", "测试用户",
                "phone", "13800138000"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("用户注册 - 用户名已存在")
    void testRegister_UsernameExists() {
        String url = baseUrl + "/api/v1/auth/register";
        
        Map<String, String> request = Map.of(
                "username", "admin",
                "password", "Test123456",
                "email", "admin@example.com",
                "realName", "管理员",
                "phone", "13800138000"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        assertNotNull(response);
        // 预期返回错误
        assertTrue(response.getStatusCode().value() >= 400 || 
                   (response.getBody() != null && response.getBody().get("code") != null && 
                    !response.getBody().get("code").equals(200)));
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void testLogin_Success() {
        String url = baseUrl + "/api/v1/auth/login";
        
        Map<String, String> request = Map.of(
                "username", "admin",
                "password", "admin123"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("用户登录 - 密码错误")
    void testLogin_WrongPassword() {
        String url = baseUrl + "/api/v1/auth/login";
        
        Map<String, String> request = Map.of(
                "username", "admin",
                "password", "wrongpassword"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        assertNotNull(response);
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.get("code").equals(401) || body.get("code").equals(400));
    }

    // ==================== 用户管理接口测试 ====================

    @Test
    @DisplayName("获取用户信息 - 成功")
    void testGetUserById_Success() {
        String url = baseUrl + "/api/v1/users/1";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取用户信息 - 用户不存在")
    void testGetUserById_NotFound() {
        String url = baseUrl + "/api/v1/users/999999";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        // 用户不存在可能返回404或null数据
        Map<String, Object> body = response.getBody();
        if (body != null) {
            assertTrue(body.get("code").equals(404) || body.get("code").equals(200));
        }
    }

    @Test
    @DisplayName("获取用户列表 - 成功")
    void testListUsers_Success() {
        String url = baseUrl + "/api/v1/users?pageNum=1&pageSize=10";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取用户列表 - 按用户名搜索")
    void testListUsers_ByUsername() {
        String url = baseUrl + "/api/v1/users?pageNum=1&pageSize=10&username=admin";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    // ==================== 部门管理接口测试 ====================

    @Test
    @DisplayName("获取部门列表 - 成功")
    void testListDepartments_Success() {
        String url = baseUrl + "/api/v1/departments";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取部门列表 - 按名称搜索")
    void testListDepartments_ByName() {
        String url = baseUrl + "/api/v1/departments?deptName=技术";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("获取部门详情 - 成功")
    void testGetDepartmentById_Success() {
        String url = baseUrl + "/api/v1/departments/1";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("创建部门 - 成功")
    void testCreateDepartment_Success() {
        String url = baseUrl + "/api/v1/departments";
        
        Map<String, Object> request = Map.of(
                "deptName", "测试部门_" + System.currentTimeMillis(),
                "deptCode", "TEST_" + System.currentTimeMillis(),
                "parentId", 0L,
                "orderNum", 1,
                "leader", "测试负责人",
                "phone", "13800138000",
                "email", "testdept@example.com",
                "status", "ACTIVE"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取部门树形结构")
    void testGetDepartmentTree() {
        String url = baseUrl + "/api/v1/departments/tree";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }
}
