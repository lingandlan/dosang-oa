package com.openoa.websocket.controller;

import com.openoa.websocket.handler.WebRTCSignalingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ws")
public class WebSocketApiController {

    /**
     * 获取WebSocket连接信息
     */
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        
        Map<String, Object> data = new HashMap<>();
        data.put("onlineUsers", WebRTCSignalingHandler.getOnlineUserCount());
        data.put("websocketEndpoint", "/ws/video/{userId}");
        
        result.put("data", data);
        return result;
    }

    /**
     * 发送信令消息（HTTP方式）
     */
    @PostMapping("/signal")
    public Map<String, Object> sendSignal(@RequestBody Map<String, Object> message) {
        try {
            String type = (String) message.get("type");
            String roomId = (String) message.get("roomId");
            String senderId = String.valueOf(message.get("senderId"));
            String targetUserId = message.get("targetUserId") != null 
                    ? String.valueOf(message.get("targetUserId")) 
                    : null;
            
            // 根据消息类型转发
            switch (type) {
                case "OFFER":
                case "ANSWER":
                case "ICE_CANDIDATE":
                    if (targetUserId != null) {
                        WebRTCSignalingHandler.sendToUser(targetUserId, 
                                createSignalMessage(type, senderId, targetUserId, roomId, message));
                    }
                    break;
                case "MUTE":
                case "UNMUTE":
                case "VIDEO_ON":
                case "VIDEO_OFF":
                    // 广播给房间内所有用户
                    break;
                default:
                    break;
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "消息已发送");
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", e.getMessage());
            return result;
        }
    }

    /**
     * 获取房间成员数量
     */
    @GetMapping("/rooms/{roomId}/members")
    public Map<String, Object> getRoomMembers(@PathVariable String roomId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        
        Map<String, Object> data = new HashMap<>();
        data.put("roomId", roomId);
        data.put("memberCount", WebRTCSignalingHandler.getRoomMemberCount(roomId));
        
        result.put("data", data);
        return result;
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "OK");
        result.put("data", Map.of("service", "websocket-service", "status", "UP"));
        return result;
    }

    private Map<String, Object> createSignalMessage(String type, String senderId, String receiverId, 
                                                     String roomId, Map<String, Object> original) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("action", type);
        message.put("senderId", senderId);
        message.put("receiverId", receiverId);
        message.put("roomId", roomId);
        message.put("payload", original);
        message.put("timestamp", System.currentTimeMillis());
        return message;
    }
}
