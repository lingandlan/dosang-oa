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

-- 扩展审批实例表
ALTER TABLE approval_instance 
ADD COLUMN template_id BIGINT COMMENT '模板ID' AFTER type_id,
ADD COLUMN current_node_id BIGINT COMMENT '当前节点ID' AFTER current_approver_id,
ADD COLUMN current_level INT DEFAULT 0 COMMENT '当前审批层级',
ADD COLUMN total_levels INT DEFAULT 0 COMMENT '总审批层级',
ADD COLUMN form_data TEXT COMMENT '表单数据JSON',
ADD INDEX idx_template_id (template_id),
ADD INDEX idx_current_node_id (current_node_id);
