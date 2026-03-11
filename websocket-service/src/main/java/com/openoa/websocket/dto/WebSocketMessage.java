package com.openoa.websocket.dto;

import lombok.Data;

@Data
public class WebSocketMessage {
    private String type;
    private String title;
    private String content;
    private Object data;
    private Long timestamp;
    
    public static WebSocketMessage of(String type, String title, String content) {
        WebSocketMessage message = new WebSocketMessage();
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setTimestamp(System.currentTimeMillis());
        return message;
    }
    
    public static WebSocketMessage of(String type, String title, String content, Object data) {
        WebSocketMessage message = of(type, title, content);
        message.setData(data);
        return message;
    }
}
