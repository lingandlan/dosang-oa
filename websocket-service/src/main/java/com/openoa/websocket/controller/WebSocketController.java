package com.openoa.websocket.controller;

import com.alibaba.fastjson2.JSON;
import com.openoa.websocket.dto.WebSocketMessage;
import com.openoa.websocket.handler.WebSocketHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ws")
public class WebSocketController {
    
    @PostMapping("/send/{userId}")
    public Map<String, Object> sendToUser(@PathVariable String userId, @RequestBody Map<String, Object> payload) {
        String type = (String) payload.get("type");
        String title = (String) payload.get("title");
        String content = (String) payload.get("content");
        Object data = payload.get("data");
        
        WebSocketMessage message;
        if (data != null) {
            message = WebSocketMessage.of(type, title, content, data);
        } else {
            message = WebSocketMessage.of(type, title, content);
        }
        
        WebSocketHandler.sendMessage(userId, JSON.toJSONString(message));
        return Map.of("code", 200, "message", "发送成功");
    }
    
    @PostMapping("/broadcast")
    public Map<String, Object> broadcast(@RequestBody Map<String, Object> payload) {
        String type = (String) payload.get("type");
        String title = (String) payload.get("title");
        String content = (String) payload.get("content");
        Object data = payload.get("data");
        
        WebSocketMessage message;
        if (data != null) {
            message = WebSocketMessage.of(type, title, content, data);
        } else {
            message = WebSocketMessage.of(type, title, content);
        }
        
        WebSocketHandler.broadcast(JSON.toJSONString(message));
        return Map.of("code", 200, "message", "广播成功");
    }
    
    @GetMapping("/online")
    public Map<String, Object> getOnlineUsers() {
        return Map.of("code", 200, "message", "success", "data", WebSocketHandler.getOnlineUsers());
    }
    
    @GetMapping("/online/count")
    public Map<String, Object> getOnlineCount() {
        return Map.of("code", 200, "message", "success", "data", WebSocketHandler.getOnlineCount());
    }
}
