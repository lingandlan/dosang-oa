package com.openoa.video.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/video")
public class VideoController {
    
    @Value("${jitsi.url}")
    private String jitsiUrl;
    
    // 创建会议室
    @PostMapping("/room")
    public Map<String, Object> createRoom(
            @RequestParam(required = false) String roomName,
            @RequestParam(required = false) Long creatorId) {
        
        String room = roomName != null ? roomName : "oa-" + UUID.randomUUID().toString().substring(0, 8);
        String meetingUrl = jitsiUrl + "/" + room;
        
        Map<String, Object> roomInfo = new HashMap<>();
        roomInfo.put("roomId", room);
        roomInfo.put("roomName", room);
        roomInfo.put("meetingUrl", meetingUrl);
        roomInfo.put("creatorId", creatorId);
        
        return Map.of("code", 200, "message", "创建成功", "data", roomInfo);
    }
    
    // 获取会议室信息
    @GetMapping("/room/{roomId}")
    public Map<String, Object> getRoom(@PathVariable String roomId) {
        String meetingUrl = jitsiUrl + "/" + roomId;
        
        Map<String, Object> roomInfo = new HashMap<>();
        roomInfo.put("roomId", roomId);
        roomInfo.put("meetingUrl", meetingUrl);
        
        return Map.of("code", 200, "message", "success", "data", roomInfo);
    }
    
    // 获取 Jitsi 配置
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        return Map.of(
                "code", 200,
                "message", "success",
                "data", Map.of("jitsiUrl", jitsiUrl)
        );
    }
}
