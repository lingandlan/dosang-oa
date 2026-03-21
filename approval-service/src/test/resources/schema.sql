-- 审批类型表
CREATE TABLE IF NOT EXISTS approval_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(100) NOT NULL,
    type_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 审批模板表
CREATE TABLE IF NOT EXISTS approval_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL,
    template_code VARCHAR(50) NOT NULL UNIQUE,
    type_id BIGINT,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 审批节点表
CREATE TABLE IF NOT EXISTS approval_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT NOT NULL,
    node_name VARCHAR(100) NOT NULL,
    node_type VARCHAR(50) NOT NULL,
    approver_type VARCHAR(50),
    approver_id BIGINT,
    sequence INT DEFAULT 0,
    condition_expr VARCHAR(500),
    timeout_hours INT DEFAULT 24,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 审批实例表
CREATE TABLE IF NOT EXISTS approval_instance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT,
    type_id BIGINT,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(100),
    current_node_id BIGINT,
    current_approver_id BIGINT,
    status VARCHAR(20) DEFAULT 'PENDING',
    priority INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 审批记录表
CREATE TABLE IF NOT EXISTS approval_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    instance_id BIGINT NOT NULL,
    node_id BIGINT,
    operator_id BIGINT NOT NULL,
    operator_name VARCHAR(100),
    action VARCHAR(20) NOT NULL,
    comment VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 初始化测试数据
INSERT INTO approval_type (type_name, type_code, description, is_active) VALUES
('请假审批', 'LEAVE', '员工请假审批流程', TRUE),
('报销审批', 'EXPENSE', '费用报销审批流程', TRUE),
('采购审批', 'PURCHASE', '物品采购审批流程', TRUE);

INSERT INTO approval_template (template_name, template_code, type_id, description, is_active) VALUES
('普通请假', 'LEAVE_NORMAL', 1, '普通员工请假审批模板', TRUE),
('部门经理请假', 'LEAVE_MANAGER', 1, '部门经理请假审批模板', TRUE),
('普通报销', 'EXPENSE_NORMAL', 2, '普通费用报销模板', TRUE);

INSERT INTO approval_node (template_id, node_name, node_type, approver_type, sequence, timeout_hours) VALUES
(1, '部门主管审批', 'APPROVER', 'MANAGER', 1, 24),
(1, '人事审批', 'APPROVER', 'HR', 2, 24),
(2, '总经理审批', 'APPROVER', 'CEO', 1, 48),
(3, '部门主管审批', 'APPROVER', 'MANAGER', 1, 24),
(3, '财务审批', 'APPROVER', 'FINANCE', 2, 24);
