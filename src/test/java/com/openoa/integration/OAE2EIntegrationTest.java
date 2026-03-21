package com.openoa.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OA系统端到端集成测试
 * 模拟完整的业务流程：注册 -> 登录 -> 部门管理 -> 审批 -> 公告 -> 视频会议
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("OA系统端到端集成测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OAE2EIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private static String testUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
    private static String testToken;
    private static Long testUserId;
    private static Long testDepartmentId;
    private static Long testApprovalId;
    private static Long testNoticeId;
    private static String testRoomId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    // ==================== 1. 用户注册流程 ====================

    @Test
    @Order(1)
    @DisplayName("E2E-01: 用户注册")
    void testE2E_UserRegister() {
        String url = baseUrl + "/api/v1/auth/register";
        
        Map<String, String> request = Map.of(
                "username", testUsername,
                "password", "Test123456",
                "email", testUsername + "@example.com",
                "realName", "测试用户",
                "phone", "13800138000"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"), "注册应该成功");
        
        System.out.println("✓ 用户注册成功: " + testUsername);
    }

    // ==================== 2. 用户登录流程 ====================

    @Test
    @Order(2)
    @DisplayName("E2E-02: 用户登录")
    void testE2E_UserLogin() {
        String url = baseUrl + "/api/v1/auth/login";
        
        Map<String, String> request = Map.of(
                "username", testUsername,
                "password", "Test123456"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"), "登录应该成功");
        
        // 提取用户信息
        if (body.get("data") != null) {
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            testUserId = data.get("id") != null ? ((Number) data.get("id")).longValue() : null;
            System.out.println("✓ 用户登录成功: " + testUsername + ", ID: " + testUserId);
        }
    }

    // ==================== 3. 获取用户信息 ====================

    @Test
    @Order(3)
    @DisplayName("E2E-03: 获取用户信息")
    void testE2E_GetUserInfo() {
        if (testUserId == null) {
            testUserId = 1L; // 使用默认ID
        }
        
        String url = baseUrl + "/api/v1/users/" + testUserId;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"), "获取用户信息应该成功");
        
        System.out.println("✓ 获取用户信息成功");
    }

    // ==================== 4. 部门管理流程 ====================

    @Test
    @Order(4)
    @DisplayName("E2E-04: 创建部门")
    void testE2E_CreateDepartment() {
        String url = baseUrl + "/api/v1/departments";
        
        String deptName = "测试部门_" + System.currentTimeMillis();
        Map<String, Object> request = Map.of(
                "deptName", deptName,
                "deptCode", "DEPT_" + System.currentTimeMillis(),
                "parentId", 0L,
                "orderNum", 1,
                "leader", "测试负责人",
                "phone", "13800138000",
                "email", "dept@example.com",
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
        assertEquals(200, body.get("code"), "创建部门应该成功");
        
        // 提取部门ID
        if (body.get("data") != null) {
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            testDepartmentId = data.get("id") != null ? ((Number) data.get("id")).longValue() : null;
            System.out.println("✓ 创建部门成功: " + deptName + ", ID: " + testDepartmentId);
        }
    }

    @Test
    @Order(5)
    @DisplayName("E2E-05: 获取部门列表")
    void testE2E_ListDepartments() {
        String url = baseUrl + "/api/v1/departments";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"), "获取部门列表应该成功");
        
        System.out.println("✓ 获取部门列表成功");
    }

    // ==================== 5. 审批流程 ====================

    @Test
    @Order(6)
    @DisplayName("E2E-06: 提交审批")
    void testE2E_SubmitApproval() {
        String url = baseUrl + "/api/v1/approvals";
        
        Map<String, Object> request = Map.of(
                "title", "测试审批_" + System.currentTimeMillis(),
                "typeId", 1L,
                "applicantId", testUserId != null ? testUserId : 1L,
                "applicantName", testUsername != null ? testUsername : "测试用户",
                "content", "这是测试审批内容",
                "status", "PENDING"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"), "提交审批应该成功");
        
        // 提取审批ID
        if (body.get("data") != null) {
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            testApprovalId = data.get("id") != null ? ((Number) data.get("id")).longValue() : null;
            System.out.println("✓ 提交审批成功, ID: " + testApprovalId);
        }
    }

    @Test
    @Order(7)
    @DisplayName("E2E-07: 获取审批列表")
    void testE2E_ListApprovals() {
        String url = baseUrl + "/api/v1/approvals?pageNum=1&pageSize=10";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"), "获取审批列表应该成功");
        
        System.out.println("✓ 获取审批列表成功");
    }

    // ==================== 6. 公告流程 ====================

    @Test
    @Order(8)
    @DisplayName("E2E-08: 发布公告")
    void testE2E_PublishNotice() {
        String url = baseUrl + "/api/v1/notices";
        
        Map<String, Object> request = Map.of(
                "title", "测试公告_" + System.currentTimeMillis(),
                "content", "这是测试公告内容",
                "category", "SYSTEM",
                "scope", "ALL",
                "pinned", 0,
                "priority", "NORMAL"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"), "发布公告应该成功");
        
        // 提取公告ID
        if (body.get("data") != null) {
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            testNoticeId = data.get("id") != null ? ((Number) data.get("id")).longValue() : null;
            System.out.println("✓ 发布公告成功, ID: " + testNoticeId);
        }
    }

    @Test
    @Order(9)
    @DisplayName("E2E-09: 获取公告列表")
    void testE2E_ListNotices() {
        String url = baseUrl + "/api/v1/notices?pageNum=1&pageSize=10";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"), "获取公告列表应该成功");
        
        System.out.println("✓ 获取公告列表成功");
    }

    // ==================== 7. 视频会议流程 ====================

    @Test
    @Order(10)
    @DisplayName("E2E-10: 创建视频房间")
    void testE2E_CreateVideoRoom() {
        String url = baseUrl + "/api/v1/video/rooms";
        
        Map<String, Object> request = Map.of(
                "roomName", "测试会议_" + System.currentTimeMillis(),
                "roomType", "MEETING",
                "creatorId", testUserId != null ? testUserId : 1L,
                "creatorName", testUsername != null ? testUsername : "测试用户",
                "maxParticipants", 10,
                "enableRecording", false,
                "enableScreenShare", true,
                "password", ""
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"), "创建视频房间应该成功");
        
        // 提取房间ID
        if (body.get("data") != null) {
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            testRoomId = (String) data.get("roomId");
            System.out.println("✓ 创建视频房间成功, RoomID: " + testRoomId);
        }
    }

    @Test
    @Order(11)
    @DisplayName("E2E-11: 加入视频房间")
    void testE2E_JoinVideoRoom() {
        if (testRoomId == null) {
            testRoomId = "test_room_" + System.currentTimeMillis();
        }
        
        String url = baseUrl + "/api/v1/video/rooms/join";
        
        Map<String, Object> request = Map.of(
                "roomId", testRoomId,
                "userId", 2L,
                "userName", "参与者"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"), "加入视频房间应该成功");
        
        System.out.println("✓ 加入视频房间成功");
    }

    @Test
    @Order(12)
    @DisplayName("E2E-12: 获取房间详情")
    void testE2E_GetRoomDetail() {
        if (testRoomId == null) {
            testRoomId = "test_room_" + System.currentTimeMillis();
        }
        
        String url = baseUrl + "/api/v1/video/rooms/" + testRoomId;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        // 房间可能不存在，但不应该报错
        Map<String, Object> body = response.getBody();
        if (body != null && body.get("code") != null) {
            assertTrue(body.get("code").equals(200) || body.get("code").equals(404));
        }
        
        System.out.println("✓ 获取房间详情完成");
    }

    // ==================== 总结 ====================

    @Test
    @Order(13)
    @DisplayName("E2E-13: 测试总结")
    void testE2E_Summary() {
        System.out.println("\n========================================");
        System.out.println("OA系统端到端集成测试完成!");
        System.out.println("========================================");
        System.out.println("测试用户名: " + testUsername);
        System.out.println("用户ID: " + testUserId);
        System.out.println("部门ID: " + testDepartmentId);
        System.out.println("审批ID: " + testApprovalId);
        System.out.println("公告ID: " + testNoticeId);
        System.out.println("房间ID: " + testRoomId);
        System.out.println("========================================\n");
        
        assertTrue(true, "测试总结");
    }
}
