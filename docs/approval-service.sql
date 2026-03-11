CREATE DATABASE IF NOT EXISTS openoa_approval DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE openoa_approval;

CREATE TABLE IF NOT EXISTS approval_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '审批类型编码',
    name VARCHAR(100) NOT NULL COMMENT '审批类型名称',
    icon VARCHAR(200) COMMENT '图标',
    config TEXT COMMENT '审批配置JSON',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批类型表';

CREATE TABLE IF NOT EXISTS approval_instance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    type_id BIGINT NOT NULL COMMENT '审批类型ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    title VARCHAR(200) NOT NULL COMMENT '审批标题',
    content TEXT COMMENT '审批内容',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/APPROVED/REJECTED',
    current_approver_id BIGINT COMMENT '当前审批人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_type_id (type_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批实例表';

CREATE TABLE IF NOT EXISTS approval_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    instance_id BIGINT NOT NULL COMMENT '审批实例ID',
    approver_id BIGINT COMMENT '审批人ID',
    action VARCHAR(20) NOT NULL COMMENT '审批动作：APPROVE/REJECT',
    comment VARCHAR(500) COMMENT '审批意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_instance_id (instance_id),
    INDEX idx_approver_id (approver_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批历史记录表';

INSERT INTO approval_type (code, name, icon, config) VALUES
('LEAVE', '请假审批', 'calendar', '{"workflow":"simple","needApproval":true}'),
('EXPENSE', '费用报销', 'money', '{"workflow":"multi","levels":2}'),
('PURCHASE', '采购申请', 'shopping', '{"workflow":"complex","needApproval":true,"budgetCheck":true}'),
('TRIP', '出差申请', 'plane', '{"workflow":"simple","needApproval":true}'),
('OVERTIME', '加班申请', 'clock', '{"workflow":"simple","needApproval":false}');
