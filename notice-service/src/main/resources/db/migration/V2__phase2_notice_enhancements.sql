-- Phase 2: Notice Service Enhancements
-- Add category, pinned, and scope fields to notice table

USE openoa_notice;

-- Add category field for notice categorization
-- Values: 'SYSTEM', 'DEPARTMENT', 'PROJECT', 'URGENT', 'INFORMATION'
ALTER TABLE notice ADD COLUMN category VARCHAR(50) COMMENT '公告分类: SYSTEM-系统公告, DEPARTMENT-部门公告, PROJECT-项目公告, URGENT-紧急通知, INFORMATION-信息通知' AFTER status;

-- Add pinned field for notice pinning
-- Values: 0-不置顶, 1-置顶
ALTER TABLE notice ADD COLUMN pinned INT DEFAULT 0 COMMENT '是否置顶: 0-不置顶, 1-置顶' AFTER category;

-- Add scope field for publication scope control
-- Values: 'ALL', 'DEPARTMENT', 'SPECIFIC'
ALTER TABLE notice ADD COLUMN scope VARCHAR(50) DEFAULT 'ALL' COMMENT '发布范围: ALL-全员, DEPARTMENT-部门内, SPECIFIC-指定人员' AFTER pinned;

-- Update existing notices with default values
UPDATE notice SET category = 'SYSTEM', pinned = 0, scope = 'ALL' WHERE category IS NULL;

-- Create index for better query performance
CREATE INDEX idx_category ON notice(category);
CREATE INDEX idx_pinned ON notice(pinned);
CREATE INDEX idx_scope ON notice(scope);