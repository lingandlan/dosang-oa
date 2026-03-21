package com.openoa.video.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openoa.video.dto.CreateRoomRequest;
import com.openoa.video.dto.JoinRoomRequest;
import com.openoa.video.dto.RoomResponse;
import com.openoa.video.entity.MeetingParticipant;
import com.openoa.video.entity.MeetingRecord;
import com.openoa.video.entity.VideoRoom;
import com.openoa.video.mapper.MeetingParticipantMapper;
import com.openoa.video.mapper.MeetingRecordMapper;
import com.openoa.video.mapper.VideoRoomMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VideoRoomService extends ServiceImpl<VideoRoomMapper, VideoRoom> {

    private final MeetingParticipantMapper participantMapper;
    private final MeetingRecordMapper recordMapper;

    public VideoRoomService(MeetingParticipantMapper participantMapper, MeetingRecordMapper recordMapper) {
        this.participantMapper = participantMapper;
        this.recordMapper = recordMapper;
    }

    /**
     * 创建视频通话房间
     */
    @Transactional
    public VideoRoom createRoom(CreateRoomRequest request) {
        VideoRoom room = new VideoRoom();
        room.setRoomId("video-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        room.setRoomName(request.getRoomName());
        room.setRoomType(request.getRoomType() != null ? request.getRoomType() : "MEETING");
        room.setCreatorId(request.getCreatorId());
        room.setCreatorName(request.getCreatorName());
        room.setDepartmentId(request.getDepartmentId());
        room.setPassword(request.getPassword());
        room.setMaxParticipants(request.getMaxParticipants() != null ? request.getMaxParticipants() : 10);
        room.setEnableRecording(request.getEnableRecording() != null ? request.getEnableRecording() : false);
        room.setEnableScreenShare(request.getEnableScreenShare() != null ? request.getEnableScreenShare() : true);
        room.setStatus("IDLE");
        room.setDuration(0);
        room.setDescription(request.getDescription());
        
        // 如果有预定的会议时间
        if (request.getScheduledStartTime() != null) {
            room.setStartTime(request.getScheduledStartTime());
            room.setEndTime(request.getScheduledEndTime());
        }
        
        save(room);
        
        // 创建会议记录
        if (request.getTitle() != null) {
            MeetingRecord record = new MeetingRecord();
            record.setRoomId(room.getId());
            record.setRoomName(room.getRoomName());
            record.setTitle(request.getTitle());
            record.setDescription(request.getDescription());
            record.setHostId(request.getCreatorId());
            record.setHostName(request.getCreatorName());
            record.setStartTime(room.getStartTime() != null ? room.getStartTime() : LocalDateTime.now());
            record.setMaxParticipants(room.getMaxParticipants());
            record.setStatus("SCHEDULED");
            recordMapper.insert(record);
        }
        
        return room;
    }

    /**
     * 加入房间
     */
    @Transactional
    public MeetingParticipant joinRoom(JoinRoomRequest request) {
        // 查找房间
        LambdaQueryWrapper<VideoRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoRoom::getRoomId, request.getRoomId());
        VideoRoom room = getOne(wrapper);
        
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        
        // 检查密码
        if (room.getPassword() != null && !room.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("房间密码错误");
        }
        
        // 检查是否已在房间中
        LambdaQueryWrapper<MeetingParticipant> participantWrapper = new LambdaQueryWrapper<>();
        participantWrapper.eq(MeetingParticipant::getRoomId, room.getId())
                .eq(MeetingParticipant::getUserId, request.getUserId())
                .eq(MeetingParticipant::getStatus, "JOINED");
        MeetingParticipant existing = participantMapper.selectOne(participantWrapper);
        
        if (existing != null) {
            return existing;
        }
        
        // 检查房间人数
        LambdaQueryWrapper<MeetingParticipant> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(MeetingParticipant::getRoomId, room.getId())
                .eq(MeetingParticipant::getStatus, "JOINED");
        long participantCount = participantMapper.selectCount(countWrapper);
        
        if (participantCount >= room.getMaxParticipants()) {
            throw new RuntimeException("房间已满");
        }
        
        // 创建参与者
        MeetingParticipant participant = new MeetingParticipant();
        participant.setRoomId(room.getId());
        participant.setUserId(request.getUserId());
        participant.setUserName(request.getUserName());
        participant.setRole(participantCount == 0 ? "HOST" : "PARTICIPANT");
        participant.setJoinTime(LocalDateTime.now());
        participant.setIsMuted(false);
        participant.setIsVideoOff(false);
        participant.setIsScreenSharing(false);
        participant.setStatus("JOINED");
        
        participantMapper.insert(participant);
        
        // 更新房间状态
        if ("IDLE".equals(room.getStatus())) {
            room.setStatus("IN_PROGRESS");
            room.setStartTime(LocalDateTime.now());
            updateById(room);
        }
        
        return participant;
    }

    /**
     * 离开房间
     */
    @Transactional
    public void leaveRoom(Long roomId, Long userId) {
        LambdaQueryWrapper<MeetingParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeetingParticipant::getRoomId, roomId)
                .eq(MeetingParticipant::getUserId, userId)
                .eq(MeetingParticipant::getStatus, "JOINED");
        
        MeetingParticipant participant = participantMapper.selectOne(wrapper);
        if (participant != null) {
            participant.setLeaveTime(LocalDateTime.now());
            participant.setStatus("LEFT");
            
            // 计算参会时长
            if (participant.getJoinTime() != null) {
                long seconds = java.time.Duration.between(participant.getJoinTime(), participant.getLeaveTime()).getSeconds();
                participant.setDuration((int) seconds);
            }
            
            participantMapper.updateById(participant);
        }
        
        // 检查房间是否还有人
        LambdaQueryWrapper<MeetingParticipant> remainingWrapper = new LambdaQueryWrapper<>();
        remainingWrapper.eq(MeetingParticipant::getRoomId, roomId)
                .eq(MeetingParticipant::getStatus, "JOINED");
        long remaining = participantMapper.selectCount(remainingWrapper);
        
        if (remaining == 0) {
            VideoRoom room = getById(roomId);
            if (room != null) {
                room.setStatus("ENDED");
                room.setEndTime(LocalDateTime.now());
                if (room.getStartTime() != null) {
                    room.setDuration((int) java.time.Duration.between(room.getStartTime(), room.getEndTime()).getSeconds());
                }
                updateById(room);
                
                // 更新会议记录
                updateMeetingRecord(roomId);
            }
        }
    }

    /**
     * 更新会议记录
     */
    private void updateMeetingRecord(Long roomId) {
        LambdaQueryWrapper<MeetingRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeetingRecord::getRoomId, roomId)
                .orderByDesc(MeetingRecord::getCreateTime)
                .last("LIMIT 1");
        
        MeetingRecord record = recordMapper.selectOne(wrapper);
        if (record != null) {
            VideoRoom room = getById(roomId);
            if (room != null) {
                record.setEndTime(room.getEndTime());
                record.setDuration(room.getDuration() != null ? room.getDuration().longValue() : null);
                record.setStatus("COMPLETED");
                
                // 统计参会人数
                LambdaQueryWrapper<MeetingParticipant> participantWrapper = new LambdaQueryWrapper<>();
                participantWrapper.eq(MeetingParticipant::getRoomId, roomId)
                        .eq(MeetingParticipant::getStatus, "LEFT");
                long count = participantMapper.selectCount(participantWrapper);
                record.setParticipantCount((int) count);
                
                recordMapper.updateById(record);
            }
        }
    }

    /**
     * 获取房间详情
     */
    public RoomResponse getRoomDetail(String roomId) {
        LambdaQueryWrapper<VideoRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoRoom::getRoomId, roomId);
        VideoRoom room = getOne(wrapper);
        
        if (room == null) {
            return null;
        }
        
        RoomResponse response = new RoomResponse();
        response.setRoomId(room.getRoomId());
        response.setRoomName(room.getRoomName());
        response.setRoomType(room.getRoomType());
        response.setCreatorId(room.getCreatorId());
        response.setCreatorName(room.getCreatorName());
        response.setMaxParticipants(room.getMaxParticipants());
        response.setEnableRecording(room.getEnableRecording());
        response.setEnableScreenShare(room.getEnableScreenShare());
        response.setStatus(room.getStatus());
        
        // 获取参与者列表
        List<MeetingParticipant> participants = participantMapper.selectList(
                new LambdaQueryWrapper<MeetingParticipant>()
                        .eq(MeetingParticipant::getRoomId, room.getId())
                        .eq(MeetingParticipant::getStatus, "JOINED")
        );
        
        response.setParticipants(participants.stream().map(p -> {
            RoomResponse.ParticipantInfo info = new RoomResponse.ParticipantInfo();
            info.setUserId(p.getUserId());
            info.setUserName(p.getUserName());
            info.setUserAvatar(p.getUserAvatar());
            info.setRole(p.getRole());
            info.setIsMuted(p.getIsMuted());
            info.setIsVideoOff(p.getIsVideoOff());
            info.setIsScreenSharing(p.getIsScreenSharing());
            return info;
        }).collect(Collectors.toList()));
        
        return response;
    }

    /**
     * 分页查询房间列表
     */
    public Page<VideoRoom> pageRooms(Integer pageNum, Integer pageSize, Long creatorId, String status) {
        Page<VideoRoom> page = new Page<>(pageNum, pageSize);
        lambdaQuery()
                .eq(creatorId != null, VideoRoom::getCreatorId, creatorId)
                .eq(status != null, VideoRoom::getStatus, status)
                .orderByDesc(VideoRoom::getCreateTime)
                .page(page);
        return page;
    }

    /**
     * 更新参与者状态
     */
    public void updateParticipantStatus(Long roomId, Long userId, String field, Object value) {
        LambdaQueryWrapper<MeetingParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeetingParticipant::getRoomId, roomId)
                .eq(MeetingParticipant::getUserId, userId)
                .eq(MeetingParticipant::getStatus, "JOINED");
        
        MeetingParticipant participant = participantMapper.selectOne(wrapper);
        if (participant != null) {
            if ("isMuted".equals(field)) {
                participant.setIsMuted((Boolean) value);
            } else if ("isVideoOff".equals(field)) {
                participant.setIsVideoOff((Boolean) value);
            } else if ("isScreenSharing".equals(field)) {
                participant.setIsScreenSharing((Boolean) value);
            }
            participantMapper.updateById(participant);
        }
    }

    /**
     * 踢出参与者
     */
    public void kickParticipant(Long roomId, Long userId) {
        leaveRoom(roomId, userId);
    }

    /**
     * 获取房间内的参与者列表
     */
    public List<MeetingParticipant> getParticipants(Long roomId) {
        return participantMapper.selectList(
                new LambdaQueryWrapper<MeetingParticipant>()
                        .eq(MeetingParticipant::getRoomId, roomId)
                        .eq(MeetingParticipant::getStatus, "JOINED")
        );
    }

    /**
     * 根据roomId获取VideoRoom
     */
    public VideoRoom getByRoomId(String roomId) {
        return lambdaQuery().eq(VideoRoom::getRoomId, roomId).one();
    }
}
