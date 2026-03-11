-- 插入审批模板示例数据
INSERT INTO approval_template (name, code, description, form_config, is_active, create_time, update_time)
VALUES 
('请假审批', 'LEAVE_APPROVAL', '员工请假审批流程', '{}', 1, NOW(), NOW()),
('报销审批', 'EXPENSE_APPROVAL', '费用报销审批流程', '{}', 1, NOW(), NOW()),
('出差审批', 'BUSINESS_TRIP_APPROVAL', '商务出差审批流程', '{}', 1, NOW(), NOW());

-- 插入审批节点示例数据 (请假审批)
INSERT INTO approval_node (template_id, name, sequence, type, approver_type, approver_config, required, create_time, update_time)
VALUES 
(1, '部门主管审批', 1, 'APPROVE', 'ROLE', 'DEPT_MANAGER', 1, NOW(), NOW()),
(1, 'HR审批', 2, 'APPROVE', 'ROLE', 'HR_MANAGER', 1, NOW(), NOW());

-- 插入审批节点示例数据 (报销审批)
INSERT INTO approval_node (template_id, name, sequence, type, approver_type, approver_config, required, create_time, update_time)
VALUES 
(2, '部门主管审批', 1, 'APPROVE', 'ROLE', 'DEPT_MANAGER', 1, NOW(), NOW()),
(2, '财务审批', 2, 'APPROVE', 'ROLE', 'FINANCE_MANAGER', 1, NOW(), NOW()),
(2, '总经理审批', 3, 'APPROVE', 'ROLE', 'GENERAL_MANAGER', 1, NOW(), NOW());

-- 插入审批节点示例数据 (出差审批)
INSERT INTO approval_node (template_id, name, sequence, type, approver_type, approver_config, required, create_time, update_time)
VALUES 
(3, '部门主管审批', 1, 'APPROVE', 'ROLE', 'DEPT_MANAGER', 1, NOW(), NOW()),
(3, '分管领导审批', 2, 'APPROVE', 'ROLE', 'VICE_MANAGER', 1, NOW(), NOW());
