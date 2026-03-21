package com.openoa.video.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openoa.video.entity.MeetingBooking;
import com.openoa.video.entity.Room;
import com.openoa.video.mapper.MeetingBookingMapper;
import com.openoa.video.mapper.RoomMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VideoService extends ServiceImpl<RoomMapper, Room> {

    private final MeetingBookingMapper meetingBookingMapper;

    public VideoService(MeetingBookingMapper meetingBookingMapper) {
        this.meetingBookingMapper = meetingBookingMapper;
    }

    public Page<Room> pageRooms(Integer pageNum, Integer pageSize, Long departmentId, String status) {
        Page<Room> page = new Page<>(pageNum, pageSize);
        lambdaQuery()
                .eq(departmentId != null, Room::getDepartmentId, departmentId)
                .eq(status != null, Room::getStatus, status)
                .orderByDesc(Room::getCreateTime)
                .page(page);
        return page;
    }

    public Page<MeetingBooking> pageBookings(Integer pageNum, Integer pageSize, Long userId, Long roomId, String status) {
        Page<MeetingBooking> page = new Page<>(pageNum, pageSize);
        meetingBookingMapper.selectPage(page, new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MeetingBooking>()
                .eq(userId != null, MeetingBooking::getUserId, userId)
                .eq(roomId != null, MeetingBooking::getRoomId, roomId)
                .eq(status != null, MeetingBooking::getStatus, status)
                .orderByDesc(MeetingBooking::getStartTime));
        return page;
    }

    public boolean checkRoomAvailability(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        Long count = meetingBookingMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MeetingBooking>()
                .eq(MeetingBooking::getRoomId, roomId)
                .in(MeetingBooking::getStatus, "CONFIRMED", "IN_PROGRESS")
                .and(wrapper -> wrapper
                        .lt(MeetingBooking::getStartTime, endTime)
                        .gt(MeetingBooking::getEndTime, startTime)));
        return count == 0;
    }

    public boolean hasBookingPermission(Long userId, Long bookingId) {
        MeetingBooking booking = meetingBookingMapper.selectById(bookingId);
        return booking != null && booking.getUserId().equals(userId);
    }

    public boolean hasRoomAccess(Long userId, Long roomId) {
        Room room = getById(roomId);
        return room != null && room.getCreatorId().equals(userId);
    }

    public MeetingBooking getBookingById(Long id) {
        return meetingBookingMapper.selectById(id);
    }

    public MeetingBookingMapper getMeetingBookingMapper() {
        return meetingBookingMapper;
    }

    public boolean saveBooking(MeetingBooking booking) {
        return meetingBookingMapper.insert(booking) > 0;
    }

    public boolean updateBooking(MeetingBooking booking) {
        return meetingBookingMapper.updateById(booking) > 0;
    }
}