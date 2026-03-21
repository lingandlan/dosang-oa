-- 审批类型表
CREATE TABLE IF NOT EXISTS approval_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '类型名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '类型编码',
    description VARCHAR(500) COMMENT '类型描述',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批类型表';

-- 审批实例表
CREATE TABLE IF NOT EXISTS approval_instance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    type_id BIGINT NOT NULL COMMENT '审批类型ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    title VARCHAR(200) NOT NULL COMMENT '审批标题',
    content TEXT COMMENT '审批内容',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '审批状态: PENDING/APPROVED/REJECTED/RETURNED',
    current_approver_id BIGINT COMMENT '当前审批人ID',
    template_id BIGINT COMMENT '模板ID',
    current_node_id BIGINT COMMENT '当前节点ID',
    current_level INT DEFAULT 0 COMMENT '当前审批层级',
    total_levels INT DEFAULT 0 COMMENT '总审批层级',
    form_data TEXT COMMENT '表单数据JSON',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_type_id (type_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_current_approver_id (current_approver_id),
    INDEX idx_template_id (template_id),
    INDEX idx_current_node_id (current_node_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批实例表';

-- 审批模板表
CREATE TABLE IF NOT EXISTS approval_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '模板名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编码',
    description VARCHAR(500) COMMENT '模板描述',
    form_config TEXT COMMENT '表单配置JSON',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批模板表';

-- 审批节点表
CREATE TABLE IF NOT EXISTS approval_node (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    template_id BIGINT NOT NULL COMMENT '模板ID',
    name VARCHAR(100) NOT NULL COMMENT '节点名称',
    sequence INT NOT NULL COMMENT '节点顺序',
    type VARCHAR(20) NOT NULL COMMENT '节点类型: APPROVE/CC/CONDITION',
    approver_type VARCHAR(20) NOT NULL COMMENT '审批人类型: USER/ROLE/DEPARTMENT/LEADER',
    approver_config TEXT COMMENT '审批人配置JSON',
    required TINYINT(1) DEFAULT 1 COMMENT '是否必须',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_template_id (template_id),
    INDEX idx_sequence (sequence)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批节点表';

-- 审批记录表
CREATE TABLE IF NOT EXISTS approval_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    instance_id BIGINT NOT NULL COMMENT '审批实例ID',
    node_id BIGINT NOT NULL COMMENT '审批节点ID',
    approver_id BIGINT NOT NULL COMMENT '审批人ID',
    approver_name VARCHAR(100) COMMENT '审批人姓名',
    action VARCHAR(20) NOT NULL COMMENT '操作类型: APPROVE/REJECT/RETURN',
    comment VARCHAR(500) COMMENT '审批意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_instance_id (instance_id),
    INDEX idx_node_id (node_id),
    INDEX idx_approver_id (approver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';
