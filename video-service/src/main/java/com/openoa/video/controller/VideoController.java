package com.openoa.video.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.video.entity.VideoParticipant;
import com.openoa.video.entity.VideoRoom;
import com.openoa.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/video")
public class VideoController {
    
    @Autowired
    private VideoService videoService;
    
    @Value("${jitsi.url}")
    private String jitsiUrl;
    
    @GetMapping("/rooms")
    public Map<String, Object> listRooms(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) String status) {
        
        Page<VideoRoom> result = videoService.getPage(pageNum, pageSize, creatorId, status);
        return Map.of("code", 200, "message", "success", "data", result);
    }
    
    @GetMapping("/rooms/active")
    public Map<String, Object> getActiveRooms() {
        return Map.of("code", 200, "message", "success", "data", videoService.getActiveRooms());
    }
    
    @GetMapping("/rooms/{id}")
    public Map<String, Object> getRoom(@PathVariable Long id) {
        return Map.of("code", 200, "message", "success", "data", videoService.getById(id));
    }
    
    @GetMapping("/room/{roomId}")
    public Map<String, Object> getRoomByRoomId(@PathVariable String roomId) {
        return Map.of("code", 200, "message", "success", "data", videoService.getByRoomId(roomId));
    }
    
    @PostMapping("/rooms")
    public Map<String, Object> createRoom(@RequestBody VideoRoom videoRoom) {
        return Map.of("code", 200, "message", "创建成功", "data", videoService.createRoom(videoRoom));
    }
    
    @PutMapping("/rooms/{id}")
    public Map<String, Object> updateRoom(@PathVariable Long id, @RequestBody VideoRoom videoRoom) {
        return Map.of("code", 200, "message", "更新成功", "data", videoService.updateRoom(id, videoRoom));
    }
    
    @PutMapping("/rooms/{id}/end")
    public Map<String, Object> endRoom(@PathVariable Long id) {
        return Map.of("code", 200, "message", "结束会议成功", "data", videoService.endRoom(id));
    }
    
    @DeleteMapping("/rooms/{id}")
    public Map<String, Object> deleteRoom(@PathVariable Long id) {
        videoService.deleteRoom(id);
        return Map.of("code", 200, "message", "删除成功");
    }
    
    @GetMapping("/rooms/{id}/participants")
    public Map<String, Object> getParticipants(@PathVariable Long id) {
        return Map.of("code", 200, "message", "success", "data", videoService.getParticipants(id));
    }
    
    @PostMapping("/rooms/{id}/join")
    public Map<String, Object> joinRoom(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam String userName) {
        return Map.of("code", 200, "message", "加入成功", "data", videoService.joinRoom(id, userId, userName));
    }
    
    @PostMapping("/rooms/{id}/leave")
    public Map<String, Object> leaveRoom(@PathVariable Long id, @RequestParam Long userId) {
        return Map.of("code", 200, "message", "离开成功", "data", videoService.leaveRoom(id, userId));
    }
    
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        return Map.of(
                "code", 200,
                "message", "success",
                "data", Map.of("jitsiUrl", jitsiUrl)
        );
    }
}
