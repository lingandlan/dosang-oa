DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `department_id` BIGINT DEFAULT NULL COMMENT '部门ID',
    `role_id` BIGINT DEFAULT NULL COMMENT '角色ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `avatar` VARCHAR(200) DEFAULT NULL COMMENT '头像URL',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_department_id` (`department_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `email`, `phone`, `department_id`, `role_id`, `status`, `remark`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', 'admin@example.com', '13800138001', 1, 1, 1, '系统管理员'),
(2, 'user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', 'zhangsan@example.com', '13800138002', 2, 2, 1, '普通用户'),
(3, 'user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四', 'lisi@example.com', '13800138003', 2, 2, 1, '普通用户'),
(4, 'user3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王五', 'wangwu@example.com', '13800138004', 3, 2, 1, '普通用户'),
(5, 'user4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵六', 'zhaoliu@example.com', '13800138005', 3, 2, 0, '普通用户-已禁用');

DROP TABLE IF EXISTS `sys_department`;

CREATE TABLE `sys_department` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `name` VARCHAR(50) NOT NULL COMMENT '部门名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID',
    `leader` VARCHAR(50) DEFAULT NULL COMMENT '部门负责人',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `leader`, `phone`, `sort`, `status`, `remark`) VALUES
(1, '总公司', 0, '张总', '010-12345678', 1, 1, '总公司'),
(2, '技术部', 1, '李经理', '010-12345679', 1, 1, '技术部门'),
(3, '市场部', 1, '王经理', '010-12345680', 2, 1, '市场部门');

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

INSERT INTO `sys_role` (`id`, `name`, `code`, `remark`) VALUES
(1, '超级管理员', 'ROLE_ADMIN', '系统管理员，拥有所有权限'),
(2, '普通用户', 'ROLE_USER', '普通用户，基本权限');
