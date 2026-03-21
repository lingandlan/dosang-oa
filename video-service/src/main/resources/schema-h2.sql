-- Video Service Database Tables for H2
-- Video Room Table
CREATE TABLE IF NOT EXISTS video_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id VARCHAR(64) NOT NULL,
    room_name VARCHAR(128) NOT NULL,
    room_key VARCHAR(128),
    room_type VARCHAR(32) DEFAULT 'MEETING',
    creator_id BIGINT NOT NULL,
    creator_name VARCHAR(64),
    department_id BIGINT,
    password VARCHAR(128),
    description VARCHAR(512),
    participant_count INT DEFAULT 0,
    participants VARCHAR(1024),
    max_participants INT DEFAULT 10,
    duration INT DEFAULT 0,
    enable_recording BOOLEAN DEFAULT FALSE,
    enable_screen_share BOOLEAN DEFAULT TRUE,
    status VARCHAR(32) DEFAULT 'IDLE',
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Meeting Participant Table
CREATE TABLE IF NOT EXISTS meeting_participant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    booking_id BIGINT,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(64),
    user_avatar VARCHAR(256),
    role VARCHAR(32) DEFAULT 'PARTICIPANT',
    join_time TIMESTAMP,
    leave_time TIMESTAMP,
    duration INT DEFAULT 0,
    is_muted BOOLEAN DEFAULT FALSE,
    is_video_off BOOLEAN DEFAULT FALSE,
    is_screen_sharing BOOLEAN DEFAULT FALSE,
    status VARCHAR(32) DEFAULT 'JOINED',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Meeting Record Table
CREATE TABLE IF NOT EXISTS meeting_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT,
    room_id BIGINT NOT NULL,
    room_name VARCHAR(128),
    title VARCHAR(256),
    description VARCHAR(512),
    host_id BIGINT,
    host_name VARCHAR(64),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    record_url VARCHAR(512),
    duration BIGINT DEFAULT 0,
    participant_count INT DEFAULT 0,
    max_participants INT DEFAULT 0,
    status VARCHAR(32) DEFAULT 'SCHEDULED',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Signaling Message Table
CREATE TABLE IF NOT EXISTS signaling_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id VARCHAR(64) NOT NULL,
    sender_id BIGINT NOT NULL,
    sender_name VARCHAR(64),
    receiver_id BIGINT,
    message_type VARCHAR(32) NOT NULL,
    payload TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Meeting Booking Table
CREATE TABLE IF NOT EXISTS meeting_booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    meeting_title VARCHAR(256),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(32) DEFAULT 'CONFIRMED',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Physical Room Table
CREATE TABLE IF NOT EXISTS room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id VARCHAR(64) NOT NULL,
    room_name VARCHAR(128) NOT NULL,
    creator_id BIGINT,
    department_id BIGINT,
    description VARCHAR(512),
    capacity INT DEFAULT 0,
    status VARCHAR(32) DEFAULT 'ACTIVE',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_video_room_creator ON video_room(creator_id);
CREATE INDEX idx_video_room_status ON video_room(status);
CREATE INDEX idx_meeting_participant_room ON meeting_participant(room_id);
CREATE INDEX idx_meeting_participant_user ON meeting_participant(user_id);
CREATE INDEX idx_meeting_record_room ON meeting_record(room_id);
CREATE INDEX idx_meeting_booking_room ON meeting_booking(room_id);
CREATE INDEX idx_room_department ON room(department_id);
