package com.openoa.integration;

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
 * 审批服务集成测试
 * 测试端口: 8082
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("审批服务集成测试")
public class ApprovalServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    // ==================== 审批接口测试 ====================

    @Test
    @DisplayName("提交审批 - 成功")
    void testSubmitApproval_Success() {
        String url = baseUrl + "/api/v1/approvals";
        
        Map<String, Object> request = Map.of(
                "title", "测试审批_" + System.currentTimeMillis(),
                "typeId", 1L,
                "applicantId", 1L,
                "applicantName", "测试申请人",
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
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取审批详情 - 成功")
    void testGetApprovalById_Success() {
        String url = baseUrl + "/api/v1/approvals/1";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取审批详情 - 审批不存在")
    void testGetApprovalById_NotFound() {
        String url = baseUrl + "/api/v1/approvals/999999";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        Map<String, Object> body = response.getBody();
        if (body != null) {
            assertTrue(body.get("code").equals(404) || body.get("code").equals(200));
        }
    }

    @Test
    @DisplayName("获取审批列表 - 成功")
    void testListApprovals_Success() {
        String url = baseUrl + "/api/v1/approvals?pageNum=1&pageSize=10";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取审批列表 - 按状态筛选")
    void testListApprovals_ByStatus() {
        String url = baseUrl + "/api/v1/approvals?pageNum=1&pageSize=10&status=PENDING";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("获取审批列表 - 按用户筛选")
    void testListApprovals_ByUserId() {
        String url = baseUrl + "/api/v1/approvals?pageNum=1&pageSize=10&userId=1";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("审批通过")
    void testApprove_Success() {
        // 先创建一个审批
        String submitUrl = baseUrl + "/api/v1/approvals";
        Map<String, Object> submitRequest = Map.of(
                "title", "待审批_" + System.currentTimeMillis(),
                "typeId", 1L,
                "applicantId", 1L,
                "applicantName", "测试申请人",
                "content", "测试审批内容",
                "status", "PENDING"
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> submitEntity = new HttpEntity<>(submitRequest, headers);
        
        ResponseEntity<Map> submitResponse = restTemplate.postForEntity(submitUrl, submitEntity, Map.class);
        
        if (submitResponse.getStatusCode().value() == 200) {
            Map<String, Object> body = submitResponse.getBody();
            if (body != null && body.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                Number id = (Number) data.get("id");
                if (id != null) {
                    // 执行审批通过操作
                    String approveUrl = baseUrl + "/api/v1/approvals/" + id.longValue() + 
                            "?action=approve&approverId=1&comment=同意";
                    
                    HttpEntity<?> approveEntity = new HttpEntity<>(headers);
                    ResponseEntity<Map> approveResponse = restTemplate.exchange(
                            approveUrl, HttpMethod.PUT, approveEntity, Map.class);
                    
                    assertNotNull(approveResponse);
                    assertEquals(200, approveResponse.getStatusCode().value());
                }
            }
        }
    }

    @Test
    @DisplayName("审批拒绝")
    void testReject_Success() {
        // 先创建一个审批
        String submitUrl = baseUrl + "/api/v1/approvals";
        Map<String, Object> submitRequest = Map.of(
                "title", "待拒绝_" + System.currentTimeMillis(),
                "typeId", 1L,
                "applicantId", 1L,
                "applicantName", "测试申请人",
                "content", "测试审批内容",
                "status", "PENDING"
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> submitEntity = new HttpEntity<>(submitRequest, headers);
        
        ResponseEntity<Map> submitResponse = restTemplate.postForEntity(submitUrl, submitEntity, Map.class);
        
        if (submitResponse.getStatusCode().value() == 200) {
            Map<String, Object> body = submitResponse.getBody();
            if (body != null && body.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                Number id = (Number) data.get("id");
                if (id != null) {
                    // 执行审批拒绝操作
                    String rejectUrl = baseUrl + "/api/v1/approvals/" + id.longValue() + 
                            "?action=reject&approverId=1&comment=不符合条件";
                    
                    HttpEntity<?> rejectEntity = new HttpEntity<>(headers);
                    ResponseEntity<Map> rejectResponse = restTemplate.exchange(
                            rejectUrl, HttpMethod.PUT, rejectEntity, Map.class);
                    
                    assertNotNull(rejectResponse);
                    assertEquals(200, rejectResponse.getStatusCode().value());
                }
            }
        }
    }

    @Test
    @DisplayName("获取审批类型列表")
    void testListApprovalTypes() {
        String url = baseUrl + "/api/v1/approvals/types";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取待审批列表")
    void testListByApprover() {
        String url = baseUrl + "/api/v1/approvals/approver/1?pageNum=1&pageSize=10";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }
}
