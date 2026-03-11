package com.openoa.video.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.video.entity.VideoRoom;
import com.openoa.video.entity.VideoParticipant;
import com.openoa.video.mapper.VideoRoomMapper;
import com.openoa.video.mapper.VideoParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {
    
    @Autowired
    private VideoRoomMapper videoRoomMapper;
    
    @Autowired
    private VideoParticipantMapper videoParticipantMapper;
    
    @Value("${jitsi.url}")
    private String jitsiUrl;
    
    public Page<VideoRoom> getPage(Integer pageNum, Integer pageSize, Long creatorId, String status) {
        Page<VideoRoom> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<VideoRoom> wrapper = new LambdaQueryWrapper<>();
        
        if (creatorId != null) {
            wrapper.eq(VideoRoom::getCreatorId, creatorId);
        }
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(VideoRoom::getStatus, status);
        }
        
        wrapper.orderByDesc(VideoRoom::getCreateTime);
        
        return videoRoomMapper.selectPage(page, wrapper);
    }
    
    public VideoRoom getById(Long id) {
        return videoRoomMapper.selectById(id);
    }
    
    public VideoRoom getByRoomId(String roomId) {
        LambdaQueryWrapper<VideoRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoRoom::getRoomId, roomId);
        return videoRoomMapper.selectOne(wrapper);
    }
    
    public VideoRoom createRoom(VideoRoom videoRoom) {
        String roomId = videoRoom.getRoomId() != null ? videoRoom.getRoomId() : "oa-" + UUID.randomUUID().toString().substring(0, 8);
        videoRoom.setRoomId(roomId);
        videoRoom.setStatus("ACTIVE");
        videoRoom.setStartTime(LocalDateTime.now());
        videoRoom.setParticipantCount(0);
        videoRoom.setMaxParticipants(videoRoom.getMaxParticipants() != null ? videoRoom.getMaxParticipants() : 50);
        videoRoom.setMeetingUrl(jitsiUrl + "/" + roomId);
        videoRoomMapper.insert(videoRoom);
        return videoRoom;
    }
    
    public VideoRoom updateRoom(Long id, VideoRoom videoRoom) {
        videoRoom.setId(id);
        videoRoomMapper.updateById(videoRoom);
        return getById(id);
    }
    
    public VideoRoom endRoom(Long id) {
        VideoRoom room = getById(id);
        if (room != null) {
            room.setStatus("ENDED");
            room.setEndTime(LocalDateTime.now());
            videoRoomMapper.updateById(room);
        }
        return room;
    }
    
    public boolean deleteRoom(Long id) {
        return videoRoomMapper.deleteById(id) > 0;
    }
    
    public List<VideoParticipant> getParticipants(Long roomId) {
        LambdaQueryWrapper<VideoParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoParticipant::getRoomId, roomId)
                .isNull(VideoParticipant::getLeaveTime)
                .orderByAsc(VideoParticipant::getJoinTime);
        return videoParticipantMapper.selectList(wrapper);
    }
    
    public VideoParticipant joinRoom(Long roomId, Long userId, String userName) {
        VideoRoom room = getById(roomId);
        if (room == null || !"ACTIVE".equals(room.getStatus())) {
            throw new RuntimeException("会议室不存在或已关闭");
        }
        
        VideoParticipant participant = new VideoParticipant();
        participant.setRoomId(roomId);
        participant.setRoomIdStr(room.getRoomId());
        participant.setUserId(userId);
        participant.setUserName(userName);
        participant.setJoinTime(LocalDateTime.now());
        participant.setRole("PARTICIPANT");
        videoParticipantMapper.insert(participant);
        
        room.setParticipantCount(room.getParticipantCount() + 1);
        videoRoomMapper.updateById(room);
        
        return participant;
    }
    
    public VideoParticipant leaveRoom(Long roomId, Long userId) {
        LambdaQueryWrapper<VideoParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoParticipant::getRoomId, roomId)
                .eq(VideoParticipant::getUserId, userId)
                .isNull(VideoParticipant::getLeaveTime);
        VideoParticipant participant = videoParticipantMapper.selectOne(wrapper);
        
        if (participant != null) {
            participant.setLeaveTime(LocalDateTime.now());
            participant.setDuration((int) java.time.Duration.between(participant.getJoinTime(), participant.getLeaveTime()).getSeconds());
            videoParticipantMapper.updateById(participant);
            
            VideoRoom room = getById(roomId);
            if (room != null && room.getParticipantCount() > 0) {
                room.setParticipantCount(room.getParticipantCount() - 1);
                videoRoomMapper.updateById(room);
            }
        }
        
        return participant;
    }
    
    public List<VideoRoom> getActiveRooms() {
        LambdaQueryWrapper<VideoRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoRoom::getStatus, "ACTIVE")
                .orderByDesc(VideoRoom::getStartTime);
        return videoRoomMapper.selectList(wrapper);
    }
}
