package com.openoa.websocket.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.openoa.websocket.dto.SignalingMessage;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@ServerEndpoint("/ws/video/{userId}")
public class WebRTCSignalingHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(WebRTCSignalingHandler.class);
    
    // 用户Session映射: userId -> Session
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();
    
    // 房间成员映射: roomId -> Set<userId>
    private static final Map<String, ConcurrentHashMap<String, Session>> roomMembers = new ConcurrentHashMap<>();
    
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        userSessions.put(userId, session);
        logger.info("用户连接: userId={}, sessionId={}", userId, session.getId());
        
        // 发送连接成功消息
        sendToSession(session, createMessage("SYSTEM", "CONNECTED", userId, null, "连接成功"));
    }
    
    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        userSessions.remove(userId);
        
        // 从所有房间中移除该用户
        for (Map.Entry<String, ConcurrentHashMap<String, Session>> entry : roomMembers.entrySet()) {
            if (entry.getValue().containsKey(userId)) {
                String roomId = entry.getKey();
                entry.getValue().remove(userId);
                
                // 通知房间内其他成员
                broadcastToRoom(roomId, createMessage("LEAVE", "USER_LEFT", userId, null, 
                        Map.of("userId", userId)));
                
                // 如果房间为空，清理房间
                if (entry.getValue().isEmpty()) {
                    roomMembers.remove(roomId);
                }
            }
        }
        
        logger.info("用户断开: userId={}", userId);
    }
    
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") String userId) {
        logger.info("收到消息: userId={}, message={}", userId, message);
        
        try {
            JSONObject json = JSON.parseObject(message);
            String type = json.getString("type");
            
            switch (type) {
                case "JOIN":
                    handleJoinRoom(json, userId);
                    break;
                case "OFFER":
                    handleOffer(json, userId);
                    break;
                case "ANSWER":
                    handleAnswer(json, userId);
                    break;
                case "ICE_CANDIDATE":
                    handleIceCandidate(json, userId);
                    break;
                case "LEAVE":
                    handleLeaveRoom(json, userId);
                    break;
                case "MUTE":
                case "UNMUTE":
                case "VIDEO_ON":
                case "VIDEO_OFF":
                case "SCREEN_SHARE_START":
                case "SCREEN_SHARE_STOP":
                    handleMediaStateChange(json, userId);
                    break;
                case "KICK":
                    handleKick(json, userId);
                    break;
                case "PING":
                    sendToSession(session, createMessage("PONG", "PONG", userId, null, "pong"));
                    break;
                default:
                    logger.warn("未知消息类型: {}", type);
            }
        } catch (Exception e) {
            logger.error("处理消息失败", e);
        }
    }
    
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("WebSocket错误", error);
    }
    
    /**
     * 处理加入房间
     */
    private void handleJoinRoom(JSONObject json, String userId) {
        String roomId = json.getString("roomId");
        String userName = json.getString("userName");
        
        // 添加用户到房间
        roomMembers.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>());
        ConcurrentHashMap<String, Session> members = roomMembers.get(roomId);
        
        // 获取用户Session
        Session userSession = userSessions.get(userId);
        if (userSession != null) {
            members.put(userId, userSession);
        }
        
        // 通知房间内其他成员有新用户加入
        broadcastToRoomExcluding(roomId, userId, createMessage("JOIN", "USER_JOINED", userId, null,
                Map.of("userId", userId, "userName", userName)));
        
        // 发送房间内现有成员列表给新用户
        List<Map<String, Object>> existingMembers = members.keySet().stream()
                .filter(id -> !id.equals(userId))
                .map(id -> {
                    Map<String, Object> member = new java.util.HashMap<>();
                    member.put("userId", id);
                    return member;
                })
                .collect(Collectors.toList());
        
        sendToUser(userId, createMessage("ROOM_MEMBERS", "MEMBERS_LIST", userId, null, 
                Map.of("roomId", roomId, "members", existingMembers)));
        
        logger.info("用户加入房间: roomId={}, userId={}", roomId, userId);
    }
    
    /**
     * 处理Offer（发起方）
     */
    private void handleOffer(JSONObject json, String userId) {
        String roomId = json.getString("roomId");
        String targetUserId = json.getString("targetUserId");
        Object sdp = json.get("sdp");
        
        // 转发Offer给目标用户
        sendToUser(targetUserId, createMessage("OFFER", "OFFER", userId, targetUserId, 
                Map.of("sdp", sdp, "senderId", userId)));
        
        logger.info("转发Offer: from={}, to={}", userId, targetUserId);
    }
    
    /**
     * 处理Answer（响应方）
     */
    private void handleAnswer(JSONObject json, String userId) {
        String roomId = json.getString("roomId");
        String targetUserId = json.getString("targetUserId");
        Object sdp = json.get("sdp");
        
        // 转发Answer给目标用户
        sendToUser(targetUserId, createMessage("ANSWER", "ANSWER", userId, targetUserId, 
                Map.of("sdp", sdp, "senderId", userId)));
        
        logger.info("转发Answer: from={}, to={}", userId, targetUserId);
    }
    
    /**
     * 处理ICE Candidate
     */
    private void handleIceCandidate(JSONObject json, String userId) {
        String roomId = json.getString("roomId");
        String targetUserId = json.getString("targetUserId");
        
        // 转发ICE Candidate给目标用户
        sendToUser(targetUserId, createMessage("ICE_CANDIDATE", "ICE_CANDIDATE", userId, targetUserId, 
                Map.of(
                        "candidate", json.get("candidate"),
                        "sdpMid", json.get("sdpMid"),
                        "sdpMLineIndex", json.get("sdpMLineIndex"),
                        "senderId", userId
                )));
        
        logger.debug("转发ICE Candidate: from={}, to={}", userId, targetUserId);
    }
    
    /**
     * 处理离开房间
     */
    private void handleLeaveRoom(JSONObject json, String userId) {
        String roomId = json.getString("roomId");
        
        ConcurrentHashMap<String, Session> members = roomMembers.get(roomId);
        if (members != null) {
            members.remove(userId);
            
            // 通知房间内其他成员
            broadcastToRoom(roomId, createMessage("LEAVE", "USER_LEFT", userId, null, 
                    Map.of("userId", userId)));
            
            // 如果房间为空，清理房间
            if (members.isEmpty()) {
                roomMembers.remove(roomId);
            }
        }
        
        logger.info("用户离开房间: roomId={}, userId={}", roomId, userId);
    }
    
    /**
     * 处理媒体状态变更（静音、开关视频等）
     */
    private void handleMediaStateChange(JSONObject json, String userId) {
        String roomId = json.getString("roomId");
        String type = json.getString("type");
        
        // 广播给房间内所有其他成员
        broadcastToRoomExcluding(roomId, userId, createMessage(type, "MEDIA_STATE_CHANGED", userId, null, 
                Map.of("userId", userId, "state", json.get("state"))));
        
        logger.info("媒体状态变更: roomId={}, userId={}, type={}", roomId, userId, type);
    }
    
    /**
     * 处理踢出用户
     */
    private void handleKick(JSONObject json, String userId) {
        String targetUserId = json.getString("targetUserId");
        String roomId = json.getString("roomId");
        
        // 通知被踢出的用户
        String reason = json.getString("reason");
        if (reason == null) {
            reason = "您已被移出房间";
        }
        sendToUser(targetUserId, createMessage("KICK", "KICKED", userId, targetUserId, 
                Map.of("reason", reason)));
        
        // 从房间中移除
        ConcurrentHashMap<String, Session> members = roomMembers.get(roomId);
        if (members != null) {
            members.remove(targetUserId);
        }
        
        // 通知房间内其他成员
        broadcastToRoom(roomId, createMessage("KICK", "USER_KICKED", userId, null, 
                Map.of("targetUserId", targetUserId)));
        
        logger.info("踢出用户: roomId={}, targetUserId={}, operator={}", roomId, targetUserId, userId);
    }
    
    /**
     * 发送消息给指定用户
     */
    public static void sendToUser(String userId, Map<String, Object> message) {
        Session session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            sendToSession(session, message);
        }
    }
    
    /**
     * 发送消息给Session
     */
    private static void sendToSession(Session session, Map<String, Object> message) {
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(JSON.toJSONString(message));
            } catch (IOException e) {
                logger.error("发送消息失败", e);
            }
        }
    }
    
    /**
     * 广播消息给房间内所有成员
     */
    private void broadcastToRoom(String roomId, Map<String, Object> message) {
        ConcurrentHashMap<String, Session> members = roomMembers.get(roomId);
        if (members != null) {
            members.values().forEach(session -> sendToSession(session, message));
        }
    }
    
    /**
     * 广播消息给房间内除指定用户外的所有成员
     */
    private void broadcastToRoomExcluding(String roomId, String excludeUserId, Map<String, Object> message) {
        ConcurrentHashMap<String, Session> members = roomMembers.get(roomId);
        if (members != null) {
            members.entrySet().stream()
                    .filter(e -> !e.getKey().equals(excludeUserId))
                    .forEach(e -> sendToSession(e.getValue(), message));
        }
    }
    
    /**
     * 创建消息格式
     */
    private Map<String, Object> createMessage(String type, String action, String senderId, String receiverId, Object payload) {
        Map<String, Object> message = new java.util.HashMap<>();
        message.put("type", type);
        message.put("action", action);
        message.put("senderId", senderId != null ? senderId : "");
        message.put("receiverId", receiverId != null ? receiverId : "");
        message.put("payload", payload != null ? payload : Map.of());
        message.put("timestamp", System.currentTimeMillis());
        return message;
    }
    
    /**
     * 获取房间成员数量
     */
    public static int getRoomMemberCount(String roomId) {
        ConcurrentHashMap<String, Session> members = roomMembers.get(roomId);
        return members != null ? members.size() : 0;
    }
    
    /**
     * 获取所有在线用户数
     */
    public static int getOnlineUserCount() {
        return userSessions.size();
    }
}
