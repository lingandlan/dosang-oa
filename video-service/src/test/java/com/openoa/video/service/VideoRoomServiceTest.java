package com.openoa.video.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.video.dto.CreateRoomRequest;
import com.openoa.video.dto.JoinRoomRequest;
import com.openoa.video.entity.MeetingParticipant;
import com.openoa.video.entity.VideoRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class VideoRoomServiceTest {

    @Autowired
    private VideoRoomService videoRoomService;

    @BeforeEach
    public void setUp() {
        videoRoomService.remove(null);
    }

    @Test
    public void testCreateRoom() {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomName("测试会议室");
        request.setRoomType("MEETING");
        request.setCreatorId(1L);
        request.setCreatorName("测试用户");
        request.setDepartmentId(1L);
        request.setMaxParticipants(10);
        request.setEnableRecording(false);
        request.setEnableScreenShare(true);
        request.setDescription("测试用会议室");

        VideoRoom room = videoRoomService.createRoom(request);

        assertNotNull(room);
        assertNotNull(room.getId());
        assertNotNull(room.getRoomId());
        assertEquals("测试会议室", room.getRoomName());
        assertEquals("IDLE", room.getStatus());
    }

    @Test
    public void testCreateRoomWithPassword() {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomName("密码会议室");
        request.setRoomType("MEETING");
        request.setCreatorId(1L);
        request.setCreatorName("测试用户");
        request.setPassword("123456");
        request.setMaxParticipants(5);

        VideoRoom room = videoRoomService.createRoom(request);

        assertNotNull(room);
        assertEquals("123456", room.getPassword());
    }

    @Test
    public void testJoinRoom() {
        // 先创建房间
        CreateRoomRequest createRequest = new CreateRoomRequest();
        createRequest.setRoomName("测试房间");
        createRequest.setCreatorId(1L);
        createRequest.setCreatorName("创建者");
        VideoRoom room = videoRoomService.createRoom(createRequest);

        String roomId = room.getRoomId();

        // 加入房间
        JoinRoomRequest joinRequest = new JoinRoomRequest();
        joinRequest.setRoomId(roomId);
        joinRequest.setUserId(2L);
        joinRequest.setUserName("参与者A");

        MeetingParticipant participant = videoRoomService.joinRoom(joinRequest);

        assertNotNull(participant);
        assertNotNull(participant.getId());
        assertEquals(2L, participant.getUserId());
        assertEquals("PARTICIPANT", participant.getRole());
    }

    @Test
    public void testJoinRoomAsHost() {
        // 先创建房间
        CreateRoomRequest createRequest = new CreateRoomRequest();
        createRequest.setRoomName("测试房间");
        createRequest.setCreatorId(1L);
        createRequest.setCreatorName("创建者");
        VideoRoom room = videoRoomService.createRoom(createRequest);

        String roomId = room.getRoomId();

        // 创建者加入房间
        JoinRoomRequest joinRequest = new JoinRoomRequest();
        joinRequest.setRoomId(roomId);
        joinRequest.setUserId(1L);
        joinRequest.setUserName("创建者");

        MeetingParticipant participant = videoRoomService.joinRoom(joinRequest);

        assertEquals("HOST", participant.getRole());
    }

    @Test
    public void testJoinRoomWithPassword() {
        // 先创建带密码的房间
        CreateRoomRequest createRequest = new CreateRoomRequest();
        createRequest.setRoomName("密码房间");
        createRequest.setCreatorId(1L);
        createRequest.setCreatorName("创建者");
        createRequest.setPassword("testpass");
        VideoRoom room = videoRoomService.createRoom(createRequest);

        String roomId = room.getRoomId();

        // 使用正确密码加入
        JoinRoomRequest joinRequest = new JoinRoomRequest();
        joinRequest.setRoomId(roomId);
        joinRequest.setUserId(2L);
        joinRequest.setUserName("参与者");
        joinRequest.setPassword("testpass");

        MeetingParticipant participant = videoRoomService.joinRoom(joinRequest);

        assertNotNull(participant);
    }

    @Test
    public void testJoinRoomWrongPassword() {
        // 先创建带密码的房间
        CreateRoomRequest createRequest = new CreateRoomRequest();
        createRequest.setRoomName("密码房间");
        createRequest.setCreatorId(1L);
        createRequest.setCreatorName("创建者");
        createRequest.setPassword("testpass");
        VideoRoom room = videoRoomService.createRoom(createRequest);

        String roomId = room.getRoomId();

        // 使用错误密码加入
        JoinRoomRequest joinRequest = new JoinRoomRequest();
        joinRequest.setRoomId(roomId);
        joinRequest.setUserId(2L);
        joinRequest.setUserName("参与者");
        joinRequest.setPassword("wrongpass");

        assertThrows(RuntimeException.class, () -> {
            videoRoomService.joinRoom(joinRequest);
        });
    }

    @Test
    public void testLeaveRoom() {
        // 先创建房间并加入
        CreateRoomRequest createRequest = new CreateRoomRequest();
        createRequest.setRoomName("测试房间");
        createRequest.setCreatorId(1L);
        createRequest.setCreatorName("创建者");
        VideoRoom room = videoRoomService.createRoom(createRequest);

        String roomId = room.getRoomId();
        Long roomDbId = room.getId();

        // 创建者加入
        JoinRoomRequest joinRequest = new JoinRoomRequest();
        joinRequest.setRoomId(roomId);
        joinRequest.setUserId(1L);
        joinRequest.setUserName("创建者");
        videoRoomService.joinRoom(joinRequest);

        // 离开房间
        videoRoomService.leaveRoom(roomDbId, 1L);

        // 验证房间状态
        VideoRoom updatedRoom = videoRoomService.getById(roomDbId);
        assertEquals("ENDED", updatedRoom.getStatus());
    }

    @Test
    public void testGetRoomDetail() {
        // 先创建房间并加入
        CreateRoomRequest createRequest = new CreateRoomRequest();
        createRequest.setRoomName("测试房间");
        createRequest.setCreatorId(1L);
        createRequest.setCreatorName("创建者");
        createRequest.setMaxParticipants(10);
        VideoRoom room = videoRoomService.createRoom(createRequest);

        String roomId = room.getRoomId();

        // 加入房间
        JoinRoomRequest joinRequest = new JoinRoomRequest();
        joinRequest.setRoomId(roomId);
        joinRequest.setUserId(1L);
        joinRequest.setUserName("创建者");
        videoRoomService.joinRoom(joinRequest);

        // 获取房间详情
        var roomResponse = videoRoomService.getRoomDetail(roomId);

        assertNotNull(roomResponse);
        assertEquals("测试房间", roomResponse.getRoomName());
        assertEquals(1, roomResponse.getParticipants().size());
    }

    @Test
    public void testPageRooms() {
        // 创建多个房间
        for (int i = 1; i <= 15; i++) {
            CreateRoomRequest request = new CreateRoomRequest();
            request.setRoomName("会议室" + i);
            request.setCreatorId(1L);
            request.setCreatorName("用户1");
            videoRoomService.createRoom(request);
        }

        // 分页查询
        Page<VideoRoom> page = videoRoomService.pageRooms(1, 10, null, null);

        assertNotNull(page);
        assertEquals(10, page.getRecords().size());
        assertEquals(15, page.getTotal());
    }

    @Test
    public void testPageRoomsByCreator() {
        // 创建不同创建者的房间
        for (int i = 1; i <= 5; i++) {
            CreateRoomRequest request = new CreateRoomRequest();
            request.setRoomName("用户1的房间" + i);
            request.setCreatorId(1L);
            request.setCreatorName("用户1");
            videoRoomService.createRoom(request);
        }

        for (int i = 1; i <= 3; i++) {
            CreateRoomRequest request = new CreateRoomRequest();
            request.setRoomName("用户2的房间" + i);
            request.setCreatorId(2L);
            request.setCreatorName("用户2");
            videoRoomService.createRoom(request);
        }

        // 按创建者查询
        Page<VideoRoom> page = videoRoomService.pageRooms(1, 10, 1L, null);

        assertNotNull(page);
        assertEquals(5, page.getRecords().size());
    }

    @Test
    public void testPageRoomsByStatus() {
        // 创建不同状态的房间
        CreateRoomRequest idleRequest = new CreateRoomRequest();
        idleRequest.setRoomName("空闲房间");
        idleRequest.setCreatorId(1L);
        idleRequest.setCreatorName("用户");
        VideoRoom idleRoom = videoRoomService.createRoom(idleRequest);

        CreateRoomRequest inProgressRequest = new CreateRoomRequest();
        inProgressRequest.setRoomName("进行中房间");
        inProgressRequest.setCreatorId(1L);
        inProgressRequest.setCreatorName("用户");
        VideoRoom inProgressRoom = videoRoomService.createRoom(inProgressRequest);

        // 将第二个房间状态设置为进行中
        inProgressRoom.setStatus("IN_PROGRESS");
        inProgressRoom.setStartTime(LocalDateTime.now());
        videoRoomService.updateById(inProgressRoom);

        // 按状态查询
        Page<VideoRoom> page = videoRoomService.pageRooms(1, 10, null, "IDLE");

        assertNotNull(page);
        assertTrue(page.getRecords().stream().allMatch(r -> "IDLE".equals(r.getStatus())));
    }

    @Test
    public void testGetParticipants() {
        // 先创建房间并加入
        CreateRoomRequest createRequest = new CreateRoomRequest();
        createRequest.setRoomName("测试房间");
        createRequest.setCreatorId(1L);
        createRequest.setCreatorName("创建者");
        VideoRoom room = videoRoomService.createRoom(createRequest);

        Long roomDbId = room.getId();
        String roomId = room.getRoomId();

        // 多人加入
        JoinRoomRequest join1 = new JoinRoomRequest();
        join1.setRoomId(roomId);
        join1.setUserId(1L);
        join1.setUserName("用户1");
        videoRoomService.joinRoom(join1);

        JoinRoomRequest join2 = new JoinRoomRequest();
        join2.setRoomId(roomId);
        join2.setUserId(2L);
        join2.setUserName("用户2");
        videoRoomService.joinRoom(join2);

        // 获取参与者列表
        var participants = videoRoomService.getParticipants(roomDbId);

        assertNotNull(participants);
        assertEquals(2, participants.size());
    }

    @Test
    public void testGetByRoomId() {
        // 先创建房间
        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomName("测试房间");
        request.setCreatorId(1L);
        request.setCreatorName("创建者");
        VideoRoom room = videoRoomService.createRoom(request);

        String roomId = room.getRoomId();

        // 按roomId查询
        VideoRoom foundRoom = videoRoomService.getByRoomId(roomId);

        assertNotNull(foundRoom);
        assertEquals(roomId, foundRoom.getRoomId());
    }
}
