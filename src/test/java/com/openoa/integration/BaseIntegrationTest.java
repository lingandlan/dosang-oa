package com.openoa.integration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

/**
 * 集成测试基类
 * 提供通用的测试辅助方法
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    /**
     * 获取带认证的请求头
     */
    protected HttpHeaders getAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null && !token.isEmpty()) {
            headers.setBearerAuth(token);
        }
        return headers;
    }

    /**
     * 发送 POST 请求
     */
    protected ResponseEntity<Map> postRequest(String url, Object body, HttpHeaders headers) {
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(url, entity, Map.class);
    }

    /**
     * 发送 GET 请求
     */
    protected ResponseEntity<Map> getRequest(String url, HttpHeaders headers) {
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
    }

    /**
     * 发送 PUT 请求
     */
    protected ResponseEntity<Map> putRequest(String url, Object body, HttpHeaders headers) {
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
    }

    /**
     * 发送 DELETE 请求
     */
    protected ResponseEntity<Map> deleteRequest(String url, HttpHeaders headers) {
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, Map.class);
    }

    /**
     * 断言成功响应
     */
    protected void assertSuccess(ResponseEntity<Map> response) {
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"), "响应码应为200");
    }

    /**
     * 断言错误响应
     */
    protected void assertError(ResponseEntity<Map> response, int expectedCode) {
        assertNotNull(response);
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(
                body.get("code").equals(expectedCode) || 
                body.get("code").equals(expectedCode / 100 * 100 + 4),
                "错误码应为" + expectedCode + "或4xx"
        );
    }

    /**
     * 获取基础URL
     */
    protected String getBaseUrl(String path) {
        return "http://localhost:" + port + path;
    }
}
