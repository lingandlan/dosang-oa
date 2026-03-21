-- Video Service Database Tables
-- Database: oa_system

USE oa_system;

-- =============================================
-- 视频通话房间表 (Video Room)
-- =============================================
CREATE TABLE IF NOT EXISTS video_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    room_id VARCHAR(64) NOT NULL COMMENT '房间唯一标识',
    room_name VARCHAR(128) NOT NULL COMMENT '房间名称',
    room_type VARCHAR(32) DEFAULT 'MEETING' COMMENT '房间类型: MEETING-会议, LIVE-直播, CONSULT-咨询',
    creator_id BIGINT NOT NULL COMMENT '创建者ID',
    creator_name VARCHAR(64) COMMENT '创建者姓名',
    department_id BIGINT COMMENT '所属部门ID',
    password VARCHAR(128) COMMENT '房间密码(可选)',
    max_participants INT DEFAULT 10 COMMENT '最大参与人数',
    enable_recording BOOLEAN DEFAULT FALSE COMMENT '是否开启录制',
    enable_screen_share BOOLEAN DEFAULT TRUE COMMENT '是否开启屏幕共享',
    status VARCHAR(32) DEFAULT 'IDLE' COMMENT '状态: IDLE-空闲, IN_PROGRESS-进行中, ENDED-已结束',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    duration INT DEFAULT 0 COMMENT '持续时间(秒)',
    description VARCHAR(512) COMMENT '房间描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '逻辑删除',
    UNIQUE KEY uk_room_id (room_id),
    INDEX idx_creator_id (creator_id),
    INDEX idx_department_id (department_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频通话房间表';

-- =============================================
-- 会议参与者表 (Meeting Participant)
-- =============================================
CREATE TABLE IF NOT EXISTS meeting_participant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(64) COMMENT '用户姓名',
    user_avatar VARCHAR(256) COMMENT '用户头像URL',
    role VARCHAR(32) DEFAULT 'PARTICIPANT' COMMENT '角色: HOST-主持人, PARTICIPANT-参与者, GUEST-访客',
    join_time DATETIME COMMENT '加入时间',
    leave_time DATETIME COMMENT '离开时间',
    duration INT DEFAULT 0 COMMENT '参会时长(秒)',
    is_muted BOOLEAN DEFAULT FALSE COMMENT '是否静音',
    is_video_off BOOLEAN DEFAULT FALSE COMMENT '是否关闭视频',
    is_screen_sharing BOOLEAN DEFAULT FALSE COMMENT '是否正在屏幕共享',
    status VARCHAR(32) DEFAULT 'JOINED' COMMENT '状态: JOINED-已加入, LEFT-已离开, KICKED-被踢出',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_room_id (room_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议参与者表';

-- =============================================
-- 会议记录表 (Meeting Record)
-- =============================================
CREATE TABLE IF NOT EXISTS meeting_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    room_name VARCHAR(128) COMMENT '房间名称',
    title VARCHAR(256) NOT NULL COMMENT '会议标题',
    description VARCHAR(512) COMMENT '会议描述',
    host_id BIGINT NOT NULL COMMENT '主持人ID',
    host_name VARCHAR(64) COMMENT '主持人姓名',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    duration INT DEFAULT 0 COMMENT '会议时长(秒)',
    participant_count INT DEFAULT 0 COMMENT '参会人数',
    max_participants INT DEFAULT 0 COMMENT '最大参会人数',
    recording_url VARCHAR(512) COMMENT '录制文件URL',
    recording_duration INT DEFAULT 0 COMMENT '录制时长(秒)',
    status VARCHAR(32) DEFAULT 'SCHEDULED' COMMENT '状态: SCHEDULED-已安排, IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_room_id (room_id),
    INDEX idx_host_id (host_id),
    INDEX idx_start_time (start_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议记录表';

-- =============================================
-- WebRTC信令消息表 (Signaling Message)
-- =============================================
CREATE TABLE IF NOT EXISTS signaling_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    room_id VARCHAR(64) NOT NULL COMMENT '房间ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    sender_name VARCHAR(64) COMMENT '发送者姓名',
    receiver_id BIGINT COMMENT '接收者ID',
    message_type VARCHAR(32) NOT NULL COMMENT '消息类型: OFFER, ANSWER, ICE_CANDIDATE, JOIN, LEAVE, KICK, MUTE, UNMUTE, VIDEO_ON, VIDEO_OFF',
    payload TEXT COMMENT '消息内容(JSON)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_room_id (room_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_message_type (message_type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WebRTC信令消息表';

-- =============================================
-- 会议室预约表 (Meeting Booking)
-- =============================================
CREATE TABLE IF NOT EXISTS meeting_booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    room_id BIGINT NOT NULL COMMENT '会议室ID',
    room_name VARCHAR(128) COMMENT '会议室名称',
    user_id BIGINT NOT NULL COMMENT '预约人ID',
    user_name VARCHAR(64) COMMENT '预约人姓名',
    department_id BIGINT COMMENT '部门ID',
    title VARCHAR(256) NOT NULL COMMENT '会议标题',
    description VARCHAR(512) COMMENT '会议描述',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    meeting_type VARCHAR(32) DEFAULT 'VIDEO' COMMENT '会议类型: VIDEO-视频会议, VOICE-语音会议, LIVE-直播',
    status VARCHAR(32) DEFAULT 'PENDING' COMMENT '状态: PENDING-待确认, CONFIRMED-已确认, IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消, REJECTED-已拒绝',
    notify_before_minutes INT DEFAULT 15 COMMENT '提前通知分钟数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_room_id (room_id),
    INDEX idx_user_id (user_id),
    INDEX idx_department_id (department_id),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time),
    INDEX idx_status (status),
    INDEX idx_time_range (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议室预约表';

-- =============================================
-- 物理会议室表 (Physical Room)
-- =============================================
CREATE TABLE IF NOT EXISTS physical_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    room_code VARCHAR(32) NOT NULL COMMENT '会议室代码',
    room_name VARCHAR(128) NOT NULL COMMENT '会议室名称',
    building VARCHAR(64) COMMENT '楼宇',
    floor INT COMMENT '楼层',
    capacity INT DEFAULT 0 COMMENT '容量',
    equipment VARCHAR(512) COMMENT '设备列表(JSON): 投影仪,白板,视频会议设备',
    status VARCHAR(32) DEFAULT 'AVAILABLE' COMMENT '状态: AVAILABLE-可用, UNAVAILABLE-不可用, MAINTENANCE-维护中',
    open_time VARCHAR(64) COMMENT '开放时间: 09:00-18:00',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_room_code (room_code),
    INDEX idx_building (building),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物理会议室表';
