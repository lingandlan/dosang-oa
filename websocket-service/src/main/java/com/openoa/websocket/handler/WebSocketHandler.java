package com.openoa.websocket.handler;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/{userId}")
public class WebSocketHandler {
    
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        sessions.put(userId, session);
        System.out.println("用户连接: " + userId);
    }
    
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        sessions.remove(userId);
        System.out.println("用户断开: " + userId);
    }
    
    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        System.out.println("收到消息: " + message);
    }
    
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
    
    // 发送消息给指定用户
    public static void sendMessage(String userId, String message) {
        Session session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 广播消息
    public static void broadcast(String message) {
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
