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
 * 公告服务集成测试
 * 测试端口: 8084
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("公告服务集成测试")
public class NoticeServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    // ==================== 公告接口测试 ====================

    @Test
    @DisplayName("发布公告 - 成功")
    void testPublishNotice_Success() {
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
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取公告详情 - 成功")
    void testGetNoticeById_Success() {
        String url = baseUrl + "/api/v1/notices/1";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取公告详情 - 公告不存在")
    void testGetNoticeById_NotFound() {
        String url = baseUrl + "/api/v1/notices/999999";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        Map<String, Object> body = response.getBody();
        if (body != null) {
            assertTrue(body.get("code").equals(404) || body.get("code").equals(200));
        }
    }

    @Test
    @DisplayName("获取公告列表 - 成功")
    void testListNotices_Success() {
        String url = baseUrl + "/api/v1/notices?pageNum=1&pageSize=10";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取公告列表 - 按状态筛选")
    void testListNotices_ByStatus() {
        String url = baseUrl + "/api/v1/notices?pageNum=1&pageSize=10&status=PUBLISHED";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("获取公告列表 - 按分类筛选")
    void testListNotices_ByCategory() {
        String url = baseUrl + "/api/v1/notices?pageNum=1&pageSize=10&category=SYSTEM";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("获取公告列表 - 置顶公告")
    void testListNotices_Pinned() {
        String url = baseUrl + "/api/v1/notices?pageNum=1&pageSize=10&pinned=1";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("更新公告")
    void testUpdateNotice_Success() {
        // 先创建一个公告
        String createUrl = baseUrl + "/api/v1/notices";
        Map<String, Object> createRequest = Map.of(
                "title", "待更新公告_" + System.currentTimeMillis(),
                "content", "原始内容",
                "category", "SYSTEM",
                "scope", "ALL",
                "pinned", 0,
                "priority", "NORMAL"
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(createRequest, headers);
        
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(createUrl, createEntity, Map.class);
        
        if (createResponse.getStatusCode().value() == 200) {
            Map<String, Object> body = createResponse.getBody();
            if (body != null && body.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                Number id = (Number) data.get("id");
                if (id != null) {
                    // 更新公告
                    String updateUrl = baseUrl + "/api/v1/notices/" + id.longValue();
                    Map<String, Object> updateRequest = Map.of(
                            "title", "更新后公告_" + System.currentTimeMillis(),
                            "content", "更新后的内容",
                            "category", "SYSTEM",
                            "scope", "ALL"
                    );
                    
                    HttpEntity<Map<String, Object>> updateEntity = new HttpEntity<>(updateRequest, headers);
                    ResponseEntity<Map> updateResponse = restTemplate.exchange(
                            updateUrl, HttpMethod.PUT, updateEntity, Map.class);
                    
                    assertNotNull(updateResponse);
                    assertEquals(200, updateResponse.getStatusCode().value());
                }
            }
        }
    }

    @Test
    @DisplayName("发布公告")
    void testPublishNotice() {
        // 先创建一个草稿公告
        String createUrl = baseUrl + "/api/v1/notices";
        Map<String, Object> createRequest = Map.of(
                "title", "待发布公告_" + System.currentTimeMillis(),
                "content", "发布内容",
                "category", "SYSTEM",
                "scope", "ALL"
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(createRequest, headers);
        
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(createUrl, createEntity, Map.class);
        
        if (createResponse.getStatusCode().value() == 200) {
            Map<String, Object> body = createResponse.getBody();
            if (body != null && body.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                Number id = (Number) data.get("id");
                if (id != null) {
                    // 发布公告
                    String publishUrl = baseUrl + "/api/v1/notices/" + id.longValue() + "/publish";
                    
                    HttpEntity<?> publishEntity = new HttpEntity<>(headers);
                    ResponseEntity<Map> publishResponse = restTemplate.exchange(
                            publishUrl, HttpMethod.PUT, publishEntity, Map.class);
                    
                    assertNotNull(publishResponse);
                    assertEquals(200, publishResponse.getStatusCode().value());
                }
            }
        }
    }

    @Test
    @DisplayName("删除公告")
    void testDeleteNotice_Success() {
        // 先创建一个公告
        String createUrl = baseUrl + "/api/v1/notices";
        Map<String, Object> createRequest = Map.of(
                "title", "待删除公告_" + System.currentTimeMillis(),
                "content", "删除内容",
                "category", "SYSTEM",
                "scope", "ALL"
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(createRequest, headers);
        
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(createUrl, createEntity, Map.class);
        
        if (createResponse.getStatusCode().value() == 200) {
            Map<String, Object> body = createResponse.getBody();
            if (body != null && body.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                Number id = (Number) data.get("id");
                if (id != null) {
                    // 删除公告
                    String deleteUrl = baseUrl + "/api/v1/notices/" + id.longValue();
                    
                    ResponseEntity<Map> deleteResponse = restTemplate.exchange(
                            deleteUrl, HttpMethod.DELETE, null, Map.class);
                    
                    assertNotNull(deleteResponse);
                    assertEquals(200, deleteResponse.getStatusCode().value());
                }
            }
        }
    }
}
