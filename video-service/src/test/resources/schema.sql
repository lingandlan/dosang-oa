-- 视频房间表
CREATE TABLE IF NOT EXISTS video_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id VARCHAR(50) NOT NULL UNIQUE,
    room_name VARCHAR(100) NOT NULL,
    room_type VARCHAR(20) DEFAULT 'MEETING',
    creator_id BIGINT NOT NULL,
    creator_name VARCHAR(100),
    department_id BIGINT,
    password VARCHAR(100),
    max_participants INT DEFAULT 10,
    enable_recording BOOLEAN DEFAULT FALSE,
    enable_screen_share BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) DEFAULT 'IDLE',
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration INT DEFAULT 0,
    description VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 会议参与者表
CREATE TABLE IF NOT EXISTS meeting_participant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(100),
    user_avatar VARCHAR(255),
    role VARCHAR(20) DEFAULT 'PARTICIPANT',
    join_time TIMESTAMP,
    leave_time TIMESTAMP,
    duration INT DEFAULT 0,
    is_muted BOOLEAN DEFAULT FALSE,
    is_video_off BOOLEAN DEFAULT FALSE,
    is_screen_sharing BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'JOINED',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 会议记录表
CREATE TABLE IF NOT EXISTS meeting_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    room_name VARCHAR(100),
    title VARCHAR(200),
    description VARCHAR(500),
    host_id BIGINT NOT NULL,
    host_name VARCHAR(100),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration INT DEFAULT 0,
    max_participants INT DEFAULT 10,
    participant_count INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 初始化测试数据
INSERT INTO video_room (room_id, room_name, room_type, creator_id, creator_name, department_id, max_participants, enable_recording, enable_screen_share, status) VALUES
('video-123456789012', '产品评审会议', 'MEETING', 1, '张三', 1, 10, TRUE, TRUE, 'IDLE'),
('video-234567890123', '周例会', 'MEETING', 2, '李四', 2, 20, FALSE, TRUE, 'IN_PROGRESS'),
('video-345678901234', '技术讨论', 'MEETING', 1, '张三', 1, 5, FALSE, TRUE, 'ENDED');

INSERT INTO meeting_participant (room_id, user_id, user_name, role, join_time, status) VALUES
(1, 1, '张三', 'HOST', '2024-01-10 10:00:00', 'JOINED'),
(1, 2, '李四', 'PARTICIPANT', '2024-01-10 10:05:00', 'JOINED'),
(2, 2, '李四', 'HOST', '2024-01-11 14:00:00', 'JOINED'),
(2, 3, '王五', 'PARTICIPANT', '2024-01-11 14:10:00', 'JOINED');

INSERT INTO meeting_record (room_id, room_name, title, host_id, host_name, start_time, max_participants, status) VALUES
(1, '产品评审会议', 'Q1产品评审', 1, '张三', '2024-01-10 10:00:00', 10, 'COMPLETED'),
(2, '周例会', '第3周周例会', 2, '李四', '2024-01-11 14:00:00', 20, 'IN_PROGRESS');
