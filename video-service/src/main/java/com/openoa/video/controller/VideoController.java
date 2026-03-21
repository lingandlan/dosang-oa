package com.openoa.video.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.video.entity.MeetingBooking;
import com.openoa.video.entity.Room;
import com.openoa.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
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
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        return response;
    }

    @GetMapping("/rooms/{id}")
    public Map<String, Object> getRoom(@PathVariable Long id) {
        Room room = videoService.getById(id);
        String meetingUrl = jitsiUrl + "/" + room.getRoomId();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("room", room);
        data.put("meetingUrl", meetingUrl);
        response.put("data", data);
        return response;
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
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        Map<String, Object> data = new HashMap<>();
        data.put("room", room);
        data.put("meetingUrl", meetingUrl);
        response.put("data", data);
        return response;
    }

    @PutMapping("/rooms/{id}")
    public Map<String, Object> updateRoom(@PathVariable Long id, @RequestBody Room room, @RequestParam Long userId) {
        if (!videoService.hasRoomAccess(userId, id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 403);
            response.put("message", "无权修改此会议室");
            return response;
        }
        room.setId(id);
        videoService.updateById(room);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", room);
        return response;
    }

    @DeleteMapping("/rooms/{id}")
    public Map<String, Object> deleteRoom(@PathVariable Long id, @RequestParam Long userId) {
        if (!videoService.hasRoomAccess(userId, id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 403);
            response.put("message", "无权删除此会议室");
            return response;
        }
        videoService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return response;
    }

    @GetMapping("/bookings")
    public Map<String, Object> listBookings(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String status) {
        Page<MeetingBooking> result = videoService.pageBookings(pageNum, pageSize, userId, roomId, status);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        return response;
    }

    @GetMapping("/bookings/{id}")
    public Map<String, Object> getBooking(@PathVariable Long id) {
        MeetingBooking booking = videoService.getBookingById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", booking);
        return response;
    }

    @PostMapping("/bookings")
    public Map<String, Object> createBooking(@RequestBody MeetingBooking booking, @RequestParam Long userId) {
        if (!videoService.checkRoomAvailability(booking.getRoomId(), booking.getStartTime(), booking.getEndTime())) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "该时间段已被预约");
            return response;
        }
        booking.setUserId(userId);
        booking.setStatus("CONFIRMED");
        videoService.saveBooking(booking);
        Room room = videoService.getById(booking.getRoomId());
        String meetingUrl = jitsiUrl + "/" + room.getRoomId();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "预约成功");
        Map<String, Object> data = new HashMap<>();
        data.put("booking", booking);
        data.put("meetingUrl", meetingUrl);
        response.put("data", data);
        return response;
    }

    @PutMapping("/bookings/{id}")
    public Map<String, Object> updateBooking(@PathVariable Long id, @RequestBody MeetingBooking booking, @RequestParam Long userId) {
        if (!videoService.hasBookingPermission(userId, id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 403);
            response.put("message", "无权修改此预约");
            return response;
        }
        if (booking.getStartTime() != null && booking.getEndTime() != null) {
            if (!videoService.checkRoomAvailability(booking.getRoomId(), booking.getStartTime(), booking.getEndTime())) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "该时间段已被预约");
                return response;
            }
        }
        booking.setId(id);
        videoService.updateBooking(booking);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", booking);
        return response;
    }

    @DeleteMapping("/bookings/{id}")
    public Map<String, Object> cancelBooking(@PathVariable Long id, @RequestParam Long userId) {
        if (!videoService.hasBookingPermission(userId, id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 403);
            response.put("message", "无权取消此预约");
            return response;
        }
        MeetingBooking booking = videoService.getBookingById(id);
        booking.setStatus("CANCELLED");
        videoService.updateBooking(booking);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "取消成功");
        return response;
    }

    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("jitsiUrl", jitsiUrl);
        response.put("data", data);
        return response;
    }
}
