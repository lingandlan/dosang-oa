-- Phase 2: Video Service - Meeting Room Booking System
-- Create tables for room and meeting booking management

USE openoa_video;

-- Create room table
CREATE TABLE IF NOT EXISTS room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    room_id VARCHAR(64) NOT NULL COMMENT '会议室ID',
    room_name VARCHAR(128) NOT NULL COMMENT '会议室名称',
    creator_id BIGINT COMMENT '创建人ID',
    department_id BIGINT COMMENT '所属部门ID',
    description VARCHAR(512) COMMENT '会议室描述',
    capacity INT DEFAULT 0 COMMENT '会议室容量',
    status VARCHAR(32) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-活跃, INACTIVE-停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_room_id (room_id),
    INDEX idx_creator_id (creator_id),
    INDEX idx_department_id (department_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议室表';

-- Create meeting_booking table
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
    status VARCHAR(32) DEFAULT 'CONFIRMED' COMMENT '状态: PENDING-待确认, CONFIRMED-已确认, IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_room_id (room_id),
    INDEX idx_user_id (user_id),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time),
    INDEX idx_status (status),
    INDEX idx_time_range (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议预约表';