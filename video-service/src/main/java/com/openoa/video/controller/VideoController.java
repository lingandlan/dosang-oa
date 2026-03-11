package com.openoa.video.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.video.entity.MeetingBooking;
import com.openoa.video.entity.Room;
import com.openoa.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/video")
public class VideoController {

    @Value("${jitsi.url}")
    private String jitsiUrl;

    @Autowired
    private VideoService videoService;

    @GetMapping("/rooms")
    public Map<String, Object> listRooms(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status) {

        Page<Room> result = videoService.pageRooms(pageNum, pageSize, departmentId, status);
        return Map.of("code", 200, "message", "success", "data", result);
    }

    @GetMapping("/rooms/{id}")
    public Map<String, Object> getRoom(@PathVariable Long id) {
        Room room = videoService.getById(id);
        String meetingUrl = jitsiUrl + "/" + room.getRoomId();
        return Map.of("code", 200, "message", "success", "data", Map.of("room", room, "meetingUrl", meetingUrl));
    }

    @PostMapping("/rooms")
    public Map<String, Object> createRoom(@RequestBody Room room, @RequestParam Long userId) {
        if (room.getRoomId() == null) {
            room.setRoomId("oa-" + UUID.randomUUID().toString().substring(0, 8));
        }
        room.setCreatorId(userId);
        room.setStatus("ACTIVE");
        videoService.save(room);
        String meetingUrl = jitsiUrl + "/" + room.getRoomId();
        return Map.of("code", 200, "message", "创建成功", "data", Map.of("room", room, "meetingUrl", meetingUrl));
    }

    @PutMapping("/rooms/{id}")
    public Map<String, Object> updateRoom(@PathVariable Long id, @RequestBody Room room, @RequestParam Long userId) {
        if (!videoService.hasRoomAccess(userId, id)) {
            return Map.of("code", 403, "message", "无权修改此会议室");
        }
        room.setId(id);
        videoService.updateById(room);
        return Map.of("code", 200, "message", "更新成功", "data", room);
    }

    @DeleteMapping("/rooms/{id}")
    public Map<String, Object> deleteRoom(@PathVariable Long id, @RequestParam Long userId) {
        if (!videoService.hasRoomAccess(userId, id)) {
            return Map.of("code", 403, "message", "无权删除此会议室");
        }
        videoService.removeById(id);
        return Map.of("code", 200, "message", "删除成功");
    }

    @GetMapping("/bookings")
    public Map<String, Object> listBookings(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String status) {

        Page<MeetingBooking> result = videoService.pageBookings(pageNum, pageSize, userId, roomId, status);
        return Map.of("code", 200, "message", "success", "data", result);
    }

    @GetMapping("/bookings/{id}")
    public Map<String, Object> getBooking(@PathVariable Long id) {
        MeetingBooking booking = videoService.getBaseMapper().selectById(id);
        return Map.of("code", 200, "message", "success", "data", booking);
    }

    @PostMapping("/bookings")
    public Map<String, Object> createBooking(@RequestBody MeetingBooking booking, @RequestParam Long userId) {
        if (!videoService.checkRoomAvailability(booking.getRoomId(), booking.getStartTime(), booking.getEndTime())) {
            return Map.of("code", 400, "message", "该时间段已被预约");
        }
        booking.setUserId(userId);
        booking.setStatus("CONFIRMED");
        videoService.save(booking);
        Room room = videoService.getById(booking.getRoomId());
        String meetingUrl = jitsiUrl + "/" + room.getRoomId();
        return Map.of("code", 200, "message", "预约成功", "data", Map.of("booking", booking, "meetingUrl", meetingUrl));
    }

    @PutMapping("/bookings/{id}")
    public Map<String, Object> updateBooking(@PathVariable Long id, @RequestBody MeetingBooking booking, @RequestParam Long userId) {
        if (!videoService.hasBookingPermission(userId, id)) {
            return Map.of("code", 403, "message", "无权修改此预约");
        }
        if (booking.getStartTime() != null && booking.getEndTime() != null) {
            if (!videoService.checkRoomAvailability(booking.getRoomId(), booking.getStartTime(), booking.getEndTime())) {
                return Map.of("code", 400, "message", "该时间段已被预约");
            }
        }
        booking.setId(id);
        videoService.updateById(booking);
        return Map.of("code", 200, "message", "更新成功", "data", booking);
    }

    @DeleteMapping("/bookings/{id}")
    public Map<String, Object> cancelBooking(@PathVariable Long id, @RequestParam Long userId) {
        if (!videoService.hasBookingPermission(userId, id)) {
            return Map.of("code", 403, "message", "无权取消此预约");
        }
        MeetingBooking booking = videoService.getBaseMapper().selectById(id);
        booking.setStatus("CANCELLED");
        videoService.updateById(booking);
        return Map.of("code", 200, "message", "取消成功");
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
