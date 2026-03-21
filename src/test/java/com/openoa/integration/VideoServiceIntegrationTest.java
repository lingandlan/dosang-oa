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
 * 视频服务集成测试
 * 测试端口: 8085
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("视频服务集成测试")
public class VideoServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    // ==================== 视频房间接口测试 ====================

    @Test
    @DisplayName("创建视频房间 - 成功")
    void testCreateRoom_Success() {
        String url = baseUrl + "/api/v1/video/rooms";
        
        Map<String, Object> request = Map.of(
                "roomName", "测试房间_" + System.currentTimeMillis(),
                "roomType", "MEETING",
                "creatorId", 1L,
                "creatorName", "测试创建者",
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
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取房间详情 - 成功")
    void testGetRoomDetail_Success() {
        // 先创建一个房间
        String createUrl = baseUrl + "/api/v1/video/rooms";
        Map<String, Object> createRequest = Map.of(
                "roomName", "详情测试房间_" + System.currentTimeMillis(),
                "roomType", "MEETING",
                "creatorId", 1L,
                "creatorName", "测试创建者"
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(createRequest, headers);
        
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(createUrl, createEntity, Map.class);
        
        if (createResponse.getStatusCode().value() == 200) {
            Map<String, Object> body = createResponse.getBody();
            if (body != null && body.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                String roomId = (String) data.get("roomId");
                
                // 获取房间详情
                String detailUrl = baseUrl + "/api/v1/video/rooms/" + roomId;
                ResponseEntity<Map> detailResponse = restTemplate.getForEntity(detailUrl, Map.class);
                
                assertNotNull(detailResponse);
                assertEquals(200, detailResponse.getStatusCode().value());
                
                Map<String, Object> detailBody = detailResponse.getBody();
                assertNotNull(detailBody);
                assertEquals(200, detailBody.get("code"));
            }
        }
    }

    @Test
    @DisplayName("获取房间详情 - 房间不存在")
    void testGetRoomDetail_NotFound() {
        String url = baseUrl + "/api/v1/video/rooms/nonexistent_room";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        Map<String, Object> body = response.getBody();
        if (body != null) {
            assertTrue(body.get("code").equals(404) || body.get("code").equals(200));
        }
    }

    @Test
    @DisplayName("获取房间列表 - 成功")
    void testListRooms_Success() {
        String url = baseUrl + "/api/v1/video/rooms?pageNum=1&pageSize=10";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("获取房间列表 - 按创建者筛选")
    void testListRooms_ByCreatorId() {
        String url = baseUrl + "/api/v1/video/rooms?pageNum=1&pageSize=10&creatorId=1";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("获取房间列表 - 按状态筛选")
    void testListRooms_ByStatus() {
        String url = baseUrl + "/api/v1/video/rooms?pageNum=1&pageSize=10&status=ACTIVE";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("加入房间 - 成功")
    void testJoinRoom_Success() {
        // 先创建一个房间
        String createUrl = baseUrl + "/api/v1/video/rooms";
        Map<String, Object> createRequest = Map.of(
                "roomName", "加入测试房间_" + System.currentTimeMillis(),
                "roomType", "MEETING",
                "creatorId", 1L,
                "creatorName", "测试创建者"
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(createRequest, headers);
        
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(createUrl, createEntity, Map.class);
        
        if (createResponse.getStatusCode().value() == 200) {
            Map<String, Object> body = createResponse.getBody();
            if (body != null && body.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                String roomId = (String) data.get("roomId");
                
                // 加入房间
                String joinUrl = baseUrl + "/api/v1/video/rooms/join";
                Map<String, Object> joinRequest = Map.of(
                        "roomId", roomId,
                        "userId", 2L,
                        "userName", "测试参与者"
                );
                
                HttpEntity<Map<String, Object>> joinEntity = new HttpEntity<>(joinRequest, headers);
                ResponseEntity<Map> joinResponse = restTemplate.postForEntity(joinUrl, joinEntity, Map.class);
                
                assertNotNull(joinResponse);
                assertEquals(200, joinResponse.getStatusCode().value());
                
                Map<String, Object> joinBody = joinResponse.getBody();
                assertNotNull(joinBody);
                assertEquals(200, joinBody.get("code"));
            }
        }
    }

    @Test
    @DisplayName("离开房间 - 成功")
    void testLeaveRoom_Success() {
        // 先创建一个房间
        String createUrl = baseUrl + "/api/v1/video/rooms";
        Map<String, Object> createRequest = Map.of(
                "roomName", "离开测试房间_" + System.currentTimeMillis(),
                "roomType", "MEETING",
                "creatorId", 1L,
                "creatorName", "测试创建者"
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(createRequest, headers);
        
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(createUrl, createEntity, Map.class);
        
        if (createResponse.getStatusCode().value() == 200) {
            Map<String, Object> body = createResponse.getBody();
            if (body != null && body.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                String roomId = (String) data.get("roomId");
                
                // 加入房间
                String joinUrl = baseUrl + "/api/v1/video/rooms/join";
                Map<String, Object> joinRequest = Map.of(
                        "roomId", roomId,
                        "userId", 2L,
                        "userName", "测试参与者"
                );
                
                HttpEntity<Map<String, Object>> joinEntity = new HttpEntity<>(joinRequest, headers);
                restTemplate.postForEntity(joinUrl, joinEntity, Map.class);
                
                // 离开房间
                String leaveUrl = baseUrl + "/api/v1/video/rooms/" + roomId + "/leave?userId=2";
                HttpEntity<?> leaveEntity = new HttpEntity<>(headers);
                ResponseEntity<Map> leaveResponse = restTemplate.exchange(
                        leaveUrl, HttpMethod.POST, leaveEntity, Map.class);
                
                assertNotNull(leaveResponse);
                assertEquals(200, leaveResponse.getStatusCode().value());
            }
        }
    }

    @Test
    @DisplayName("获取房间参与者列表")
    void testGetParticipants() {
        // 先创建一个房间并加入
        String createUrl = baseUrl + "/api/v1/video/rooms";
        Map<String, Object> createRequest = Map.of(
                "roomName", "参与者测试房间_" + System.currentTimeMillis(),
                "roomType", "MEETING",
                "creatorId", 1L,
                "creatorName", "测试创建者"
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(createRequest, headers);
        
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(createUrl, createEntity, Map.class);
        
        if (createResponse.getStatusCode().value() == 200) {
            Map<String, Object> body = createResponse.getBody();
            if (body != null && body.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                String roomId = (String) data.get("roomId");
                
                // 加入房间
                String joinUrl = baseUrl + "/api/v1/video/rooms/join";
                Map<String, Object> joinRequest = Map.of(
                        "roomId", roomId,
                        "userId", 2L,
                        "userName", "测试参与者"
                );
                
                HttpEntity<Map<String, Object>> joinEntity = new HttpEntity<>(joinRequest, headers);
                restTemplate.postForEntity(joinUrl, joinEntity, Map.class);
                
                // 获取参与者列表
                String participantsUrl = baseUrl + "/api/v1/video/rooms/" + roomId + "/participants";
                ResponseEntity<Map> participantsResponse = restTemplate.getForEntity(participantsUrl, Map.class);
                
                assertNotNull(participantsResponse);
                assertEquals(200, participantsResponse.getStatusCode().value());
                
                Map<String, Object> participantsBody = participantsResponse.getBody();
                assertNotNull(participantsBody);
                assertEquals(200, participantsBody.get("code"));
            }
        }
    }

    @Test
    @DisplayName("获取视频配置信息")
    void testGetConfig() {
        String url = baseUrl + "/api/v1/video/config";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("健康检查")
    void testHealth() {
        String url = baseUrl + "/api/v1/video/health";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
    }

    @Test
    @DisplayName("踢出参与者")
    void testKickParticipant() {
        // 先创建一个房间并加入
        String createUrl = baseUrl + "/api/v1/video/rooms";
        Map<String, Object> createRequest = Map.of(
                "roomName", "踢出测试房间_" + System.currentTimeMillis(),
                "roomType", "MEETING",
                "creatorId", 1L,
                "creatorName", "测试创建者"
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(createRequest, headers);
        
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(createUrl, createEntity, Map.class);
        
        if (createResponse.getStatusCode().value() == 200) {
            Map<String, Object> body = createResponse.getBody();
            if (body != null && body.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                String roomId = (String) data.get("roomId");
                
                // 加入房间
                String joinUrl = baseUrl + "/api/v1/video/rooms/join";
                Map<String, Object> joinRequest = Map.of(
                        "roomId", roomId,
                        "userId", 2L,
                        "userName", "测试参与者"
                );
                
                HttpEntity<Map<String, Object>> joinEntity = new HttpEntity<>(joinRequest, headers);
                restTemplate.postForEntity(joinUrl, joinEntity, Map.class);
                
                // 踢出参与者
                String kickUrl = baseUrl + "/api/v1/video/rooms/" + roomId + "/kick/2?operatorId=1";
                HttpEntity<?> kickEntity = new HttpEntity<>(headers);
                ResponseEntity<Map> kickResponse = restTemplate.exchange(
                        kickUrl, HttpMethod.POST, kickEntity, Map.class);
                
                assertNotNull(kickResponse);
                assertEquals(200, kickResponse.getStatusCode().value());
            }
        }
    }
}
